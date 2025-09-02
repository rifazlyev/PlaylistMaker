package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_in_playlist")
data class TrackInPlaylistEntity(
    @PrimaryKey
    val trackId: Long,
    val artworkUrl100: String,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val trackTime: Long,
    val previewUrl: String?,
    val addedAt: Long
)
