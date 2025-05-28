package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.model.TrackUi

interface TrackHistoryRepository {
    fun loadHistoryTrackList(): MutableList<Track>
    fun addTrackToSearchHistoryList(track: Track)
    fun clearSearchHistoryTrackList()
}
