package com.example.playlistmaker

import com.example.playlistmaker.domain.models.Track

interface OnTrackClickListener {
    fun onTrackClick(track: Track)
}
