package com.example.playlistmaker.presentation.mapper

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.model.TrackUi

fun TrackUi.toTrackDomain(): Track {
    return Track(
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl100 = artworkUrl100,
        trackId = trackId,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}