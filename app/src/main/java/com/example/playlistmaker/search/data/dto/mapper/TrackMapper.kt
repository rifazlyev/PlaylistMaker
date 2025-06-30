package com.example.playlistmaker.search.data.dto.mapper

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.Track

fun TrackDto.toTrackDomain(): Track {
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
