package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

import com.example.playlistmaker.search.ui.model.TrackUi

sealed interface PlaylistDetailsUiState {
    data object Empty : PlaylistDetailsUiState
    data class Content(val tracks: List<TrackUi>) : PlaylistDetailsUiState
}
