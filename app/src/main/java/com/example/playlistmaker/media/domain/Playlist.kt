package com.example.playlistmaker.media.domain

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val coverPath: String? = null,
    val trackIds: String = "",
    val tracksCount: Int = 0
)
