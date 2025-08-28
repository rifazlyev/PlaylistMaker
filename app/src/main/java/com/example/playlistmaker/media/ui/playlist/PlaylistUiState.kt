package com.example.playlistmaker.media.ui.playlist

import com.example.playlistmaker.media.ui.model.PlaylistUi

sealed interface PlaylistUiState {
    data object Empty: PlaylistUiState
    data class Content(val playlists: List<PlaylistUi>): PlaylistUiState
}
