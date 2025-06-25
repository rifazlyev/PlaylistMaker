package com.example.playlistmaker

import com.example.playlistmaker.presentation.model.TrackUi

sealed interface TrackUiState {
    object Loading : TrackUiState
    data class Content(val tracks: List<TrackUi>) : TrackUiState
    object Error : TrackUiState
    object EmptyResult : TrackUiState
    object EmptyScreen: TrackUiState
    data class HistoryContent(val tracks: List<TrackUi>): TrackUiState
}
