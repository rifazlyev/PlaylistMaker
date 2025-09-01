package com.example.playlistmaker.media.data.converter

import com.example.playlistmaker.media.data.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker.media.domain.model.TrackInPlaylist

class TrackInPlaylistDbConverter {
    fun map(trackInPlaylistEntity: TrackInPlaylistEntity): TrackInPlaylist {
        return TrackInPlaylist(
            trackId = trackInPlaylistEntity.trackId,
            artworkUrl100 = trackInPlaylistEntity.artworkUrl100,
            trackName = trackInPlaylistEntity.trackName,
            artistName = trackInPlaylistEntity.artistName,
            collectionName = trackInPlaylistEntity.collectionName,
            releaseDate = trackInPlaylistEntity.releaseDate,
            primaryGenreName = trackInPlaylistEntity.primaryGenreName,
            country = trackInPlaylistEntity.country,
            trackTime = trackInPlaylistEntity.trackTime,
            formattedTime = trackInPlaylistEntity.formattedTime,
            previewUrl = trackInPlaylistEntity.previewUrl,
            )
    }

    fun map(trackInPlaylist: TrackInPlaylist): TrackInPlaylistEntity{
        return TrackInPlaylistEntity(
            trackId = trackInPlaylist.trackId,
            artworkUrl100 = trackInPlaylist.artworkUrl100,
            trackName = trackInPlaylist.trackName,
            artistName = trackInPlaylist.artistName,
            collectionName = trackInPlaylist.collectionName,
            releaseDate = trackInPlaylist.releaseDate,
            primaryGenreName = trackInPlaylist.primaryGenreName,
            country = trackInPlaylist.country,
            trackTime = trackInPlaylist.trackTime,
            formattedTime = trackInPlaylist.formattedTime,
            previewUrl = trackInPlaylist.previewUrl,
            addedAt = System.currentTimeMillis()
        )
    }
}
