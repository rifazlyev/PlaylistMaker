package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface PlayerInteractor {
    fun getTrackById(id: Int): Track
}
