package com.example.playlistmaker.player.ui

import com.example.playlistmaker.media.ui.model.PlaylistUi

sealed interface PlayerPlaylistState {
    data object Empty : PlayerPlaylistState
    data class Content(val playlists: List<PlaylistUi>) : PlayerPlaylistState
}
