package com.example.playlistmaker.media.domain.model

data class TrackInPlaylist(
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
    )
