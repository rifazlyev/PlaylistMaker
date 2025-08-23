package com.example.playlistmaker.media.data.converter

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.Playlist

class PlaylistDbConverter {
    fun map(playlistEntity: PlaylistEntity): Playlist{
        return Playlist(
            id = playlistEntity.id,
            name = playlistEntity.name,
            description = playlistEntity.description,
            coverPath = playlistEntity.coverPath,
            trackIds = playlistEntity.trackIds,
            tracksCount = playlistEntity.tracksCount
        )
    }

    fun map(playlist: Playlist): PlaylistEntity{
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = playlist.trackIds,
            tracksCount = playlist.tracksCount
        )
    }
}
