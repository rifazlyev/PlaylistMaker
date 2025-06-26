package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.TrackHistoryRepository
import com.example.playlistmaker.search.domain.Track

class PlayerInteractorImpl(private val trackHistoryRepository: TrackHistoryRepository):
    PlayerInteractor {
    override fun getTrackById(id: Int): Track = trackHistoryRepository.getTrackById(id)
}
