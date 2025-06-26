package com.example.playlistmaker.search.domain

interface TrackInteractor {
    fun searchTrack(expression: String, consumer: TrackConsumer)

    interface TrackConsumer{
        fun consume(foundTrack: List<Track>?, errorMessage: String?)
    }
    fun loadHistoryTrackList(): MutableList<Track>
    fun addTrackToSearchHistoryList(track: Track)
    fun clearSearchHistoryTrackList()
}
