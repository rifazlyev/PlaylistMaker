package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

sealed interface PlaylistDetailsEvent {
    data object ShowNoTracksToast: PlaylistDetailsEvent
}
