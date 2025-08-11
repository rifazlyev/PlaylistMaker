package com.example.playlistmaker.search.domain

import com.example.playlistmaker.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(
    private val trackRepository: TrackRepository,
    private val trackHistoryRepository: TrackHistoryRepository
) : TrackInteractor {

    override fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>> {
        return trackRepository.searchTrack(expression).map { result ->
            when (result) {
                is Resource.Success -> Pair(result.data, null)
                is Resource.Error -> Pair(null, result.message)
            }
        }
    }

    override fun loadHistoryTrackList(): MutableList<Track> {
        return trackHistoryRepository.loadHistoryTrackList()
    }

    override fun addTrackToSearchHistoryList(track: Track) {
        trackHistoryRepository.addTrackToSearchHistoryList(track)
    }

    override fun clearSearchHistoryTrackList() {
        trackHistoryRepository.clearSearchHistoryTrackList()
    }
}
