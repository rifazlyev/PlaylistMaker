package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

import com.example.playlistmaker.search.ui.model.TrackUi

interface OnTrackInPlaylistLongClickListener {
    fun onTrackLongClickListener(trackUi: TrackUi): Boolean
}
