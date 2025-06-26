package com.example.playlistmaker.search.domain

interface TrackHistoryRepository {
    fun loadHistoryTrackList(): MutableList<Track>
    fun addTrackToSearchHistoryList(track: Track)
    fun clearSearchHistoryTrackList()
    fun getTrackById(id: Int): Track
}
