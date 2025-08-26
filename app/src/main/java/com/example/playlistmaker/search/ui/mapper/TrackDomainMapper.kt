package com.example.playlistmaker.search.ui.mapper

import com.example.playlistmaker.media.domain.model.TrackInPlaylist
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.model.TrackUi

fun TrackUi.toTrackDomain(isFavorite: Boolean): Track {
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
        previewUrl = previewUrl,
        isFavorite = isFavorite
    )
}

fun TrackUi.toTrackInPlaylist(): TrackInPlaylist{
    return TrackInPlaylist(
        trackId = trackId,
        artworkUrl100 = artworkUrl100,
        trackName = trackName,
        artistName = artistName,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        trackTime = trackTime,
        formattedTime = formattedTime,
        previewUrl = previewUrl
    )
}
