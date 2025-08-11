package com.example.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>>
    fun loadHistoryTrackList(): MutableList<Track>
    fun addTrackToSearchHistoryList(track: Track)
    fun clearSearchHistoryTrackList()
}
