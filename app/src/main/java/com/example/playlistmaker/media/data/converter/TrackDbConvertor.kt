package com.example.playlistmaker.media.data.converter

import com.example.playlistmaker.common.formatTrackTime
import com.example.playlistmaker.media.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            formattedTime = formatTrackTime(track.trackTime),
            addedAt = System.currentTimeMillis()
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = true
        )
    }
}
