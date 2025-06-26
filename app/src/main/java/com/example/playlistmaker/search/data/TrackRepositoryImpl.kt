package com.example.playlistmaker.search.data

import com.example.playlistmaker.common.Resource
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.dto.mapper.toTrackDomain
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.search.domain.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTrack(
        expression: String
    ): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                Resource.Success((response as TrackSearchResponse).results.map {
                    it.toTrackDomain()
                })
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}
