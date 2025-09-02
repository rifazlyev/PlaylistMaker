package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

import com.example.playlistmaker.search.ui.model.TrackUi

sealed interface PlaylistDetailsUiState {
    data class Empty(val track: List<TrackUi>) : PlaylistDetailsUiState
    data class Content(val track: List<TrackUi>) : PlaylistDetailsUiState
}
