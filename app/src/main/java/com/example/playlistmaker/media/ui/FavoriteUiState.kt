package com.example.playlistmaker.media.ui
import com.example.playlistmaker.search.ui.model.TrackUi

interface FavoriteUiState {
    object Empty : FavoriteUiState
    data class Content(val tracks: List<TrackUi>): FavoriteUiState
}
