package com.example.playlistmaker.player.ui

import com.example.playlistmaker.media.ui.model.PlaylistUi

sealed interface AddTrackResult {
    data class Success(val playlist: PlaylistUi) : AddTrackResult
    data class AlreadyExist(val playlist: PlaylistUi) : AddTrackResult
    data object Error : AddTrackResult
}
