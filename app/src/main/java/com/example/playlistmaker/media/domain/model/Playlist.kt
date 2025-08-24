package com.example.playlistmaker.media.domain.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val coverPath: String? = null,
    val trackIds: List<Int> = emptyList(),
    val tracksCount: Int = 0
)
