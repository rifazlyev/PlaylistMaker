package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val coverPath: String? = null,
    val trackIds: String = "[]",
    val tracksCount: Int= 0
)