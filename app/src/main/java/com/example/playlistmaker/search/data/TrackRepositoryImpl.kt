package com.example.playlistmaker.search.data

import com.example.playlistmaker.common.Resource
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.dto.mapper.toTrackDomain
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTrack(
        expression: String
    ): Flow<Resource<List<Track>>> = flow{
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                emit(Resource.Success((response as TrackSearchResponse).results.map {
                    it.toTrackDomain()
                }))
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}
