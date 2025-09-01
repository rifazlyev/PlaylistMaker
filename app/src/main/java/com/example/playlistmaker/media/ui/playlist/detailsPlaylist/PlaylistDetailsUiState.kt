package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

import com.example.playlistmaker.media.ui.model.TrackInPlaylistUi

sealed interface PlaylistDetailsUiState {
    data class Empty(val track: List<TrackInPlaylistUi>) : PlaylistDetailsUiState
    data class Content(val track: List<TrackInPlaylistUi>) : PlaylistDetailsUiState
}
