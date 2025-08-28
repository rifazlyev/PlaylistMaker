package com.example.playlistmaker.search.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackUi(
    val trackName: String,
    val artistName: String,
    val trackTime: Long,
    val formattedTime: String,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?,
    val isFavorite: Boolean
) : Parcelable {
    fun getCoverArtwork(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }
}
