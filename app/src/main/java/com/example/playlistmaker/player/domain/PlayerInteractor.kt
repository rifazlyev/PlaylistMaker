package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.Track

interface PlayerInteractor {
    fun getTrackById(id: Int): Track
}
