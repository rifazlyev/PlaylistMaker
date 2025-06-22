package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.models.Track

class PlayerInteractorImpl(private val trackHistoryRepository: TrackHistoryRepository): PlayerInteractor {
    override fun getTrackById(id: Int): Track = trackHistoryRepository.getTrackById(id)
}
