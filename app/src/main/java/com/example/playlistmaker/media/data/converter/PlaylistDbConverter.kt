package com.example.playlistmaker.media.data.converter

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter(private val gson: Gson) {
    fun map(playlistEntity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Long>>() {}.type
        val trackIds = if (playlistEntity.trackIds.isBlank()) {
            emptyList<Long>()
        } else {
            gson.fromJson(playlistEntity.trackIds, type)
        }
        return Playlist(
            id = playlistEntity.id,
            name = playlistEntity.name,
            description = playlistEntity.description,
            coverPath = playlistEntity.coverPath,
            trackIds = trackIds,
            tracksCount = trackIds.size
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        val json: String = gson.toJson(playlist.trackIds)
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = json,
            tracksCount = playlist.trackIds.size
        )
    }
}
