package com.example.playlistmaker.media.ui.playlist.createPlaylist

import com.example.playlistmaker.media.ui.model.PlaylistUi

sealed interface PlaylistConditionUiState {
    data object Creation : PlaylistConditionUiState
    data class Editing(val playlist: PlaylistUi) : PlaylistConditionUiState
}
