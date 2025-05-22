package com.example.playlistmaker.presentation.mapper

import com.example.playlistmaker.UiUtils.formatTrackTime
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.model.TrackUi

fun Track.toTrackUi(): TrackUi {
    return TrackUi(
        trackName = trackName,
        artistName = artistName,
        formattedTime = formatTrackTime(trackTime),
        artworkUrl100 = artworkUrl100,
        trackId = trackId,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}