package com.example.playlistmaker.media.ui.model

data class TrackInPlaylistUi(
    val trackId: Long,
    val artworkUrl100: String,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val trackTime: Long,
    val formattedTime: String,
    val previewUrl: String?,
    )
