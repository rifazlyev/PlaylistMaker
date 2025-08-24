package com.example.playlistmaker.media.ui.mapper

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.model.PlaylistUi

fun Playlist.toPlaylistUi(): PlaylistUi {
    return PlaylistUi(
        id = id,
        name = name,
        description = description,
        coverPath = coverPath,
        trackIds = trackIds,
        tracksCount = tracksCount
    )
}

fun PlaylistUi.toPlaylist(): Playlist {
    return Playlist(
        id = id,
        name = name,
        description = description,
        coverPath = coverPath,
        trackIds = trackIds,
        tracksCount = tracksCount
    )
}

