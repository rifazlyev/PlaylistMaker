package com.example.playlistmaker.search.ui.mapper

import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.model.TrackUi

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