package com.example.playlistmaker.media.ui.model

data class PlaylistUi(
    val id: Long,
    val name: String,
    val description: String = "",
    val coverPath: String? = null,
    val trackIds: List<Int> = emptyList(),
    val tracksCount: Int = trackIds.size
)
