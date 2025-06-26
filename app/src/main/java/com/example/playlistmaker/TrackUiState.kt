package com.example.playlistmaker

import com.example.playlistmaker.presentation.model.TrackUi

sealed interface TrackUiState {
    data object Loading : TrackUiState
    data class Content(val tracks: List<TrackUi>) : TrackUiState
    data object Error : TrackUiState
    data object EmptyResult : TrackUiState
    data object EmptyScreen: TrackUiState
    data class HistoryContent(val tracks: List<TrackUi>): TrackUiState
}
