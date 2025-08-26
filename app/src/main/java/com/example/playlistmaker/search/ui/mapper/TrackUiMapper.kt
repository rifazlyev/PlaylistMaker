package com.example.playlistmaker.search.ui.mapper

import com.example.playlistmaker.common.formatTrackTime
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.model.TrackUi

fun Track.toTrackUi(): TrackUi {
    return TrackUi(
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        formattedTime = formatTrackTime(trackTime),
        artworkUrl100 = artworkUrl100,
        trackId = trackId,
        collectionName = collectionName,
        releaseDate = releaseDate?.take(4),
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl,
        isFavorite = isFavorite
    )
}