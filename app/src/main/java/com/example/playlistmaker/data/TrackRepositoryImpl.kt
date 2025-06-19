package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.data.mapper.toTrackDomain
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTrack(expression: String, callback: (Result<List<Track>>) -> Unit) {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200 && response is TrackSearchResponse) {
            val trackDomainList = response.results.map { it.toTrackDomain() }
            callback(Result.success(trackDomainList))
        } else {
            callback(Result.failure(Exception(response.resultCode.toString())))
        }
    }
}