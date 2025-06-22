package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackHistoryRepository {
    fun loadHistoryTrackList(): MutableList<Track>
    fun addTrackToSearchHistoryList(track: Track)
    fun clearSearchHistoryTrackList()
    fun getTrackById(id: Int): Track
}
