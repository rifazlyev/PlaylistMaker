package com.example.playlistmaker.media.data.converter

import com.example.playlistmaker.media.data.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker.search.domain.Track

class TrackInPlaylistDbConverter {
    fun map(trackInPlaylistEntity: TrackInPlaylistEntity): Track {
        return Track(
            trackId = trackInPlaylistEntity.trackId,
            artworkUrl100 = trackInPlaylistEntity.artworkUrl100,
            trackName = trackInPlaylistEntity.trackName,
            artistName = trackInPlaylistEntity.artistName,
            collectionName = trackInPlaylistEntity.collectionName,
            releaseDate = trackInPlaylistEntity.releaseDate,
            primaryGenreName = trackInPlaylistEntity.primaryGenreName,
            country = trackInPlaylistEntity.country,
            trackTime = trackInPlaylistEntity.trackTime,
            previewUrl = trackInPlaylistEntity.previewUrl,
            )
    }

    fun map(track: Track): TrackInPlaylistEntity{
        return TrackInPlaylistEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTime = track.trackTime,
            previewUrl = track.previewUrl,
            addedAt = System.currentTimeMillis()
        )
    }
}
