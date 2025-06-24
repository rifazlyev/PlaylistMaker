package com.example.playlistmaker

import com.example.playlistmaker.domain.models.Track

sealed interface TrackUiState {
    object Loading : TrackUiState
    data class Content(val tracks: List<Track>) : TrackUiState
    object Error : TrackUiState
    object Empty : TrackUiState
    data class History(val tracks: List<Track>) : TrackUiState
}
