package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class TrackEntity(
    @PrimaryKey
    val trackId: Int,
    val artworkUrl100: String,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val trackTime: Long,
    val formattedTime: String,
    val previewUrl: String?,
    //Для сортировки по времени добавления
    val addedAt: Long
)
