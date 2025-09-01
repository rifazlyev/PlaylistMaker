package com.example.playlistmaker.media.ui.mapper

import com.example.playlistmaker.media.domain.model.TrackInPlaylist
import com.example.playlistmaker.media.ui.model.TrackInPlaylistUi

fun TrackInPlaylist.toTrackInPlaylistUi(): TrackInPlaylistUi{
    return TrackInPlaylistUi(
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
        previewUrl = previewUrl,
        )
}
