package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

import com.example.playlistmaker.media.ui.model.TrackInPlaylistUi

sealed interface PlaylistDetailsUiState {
    data object Empty : PlaylistDetailsUiState
    data class Content(val track: List<TrackInPlaylistUi>) : PlaylistDetailsUiState
}
