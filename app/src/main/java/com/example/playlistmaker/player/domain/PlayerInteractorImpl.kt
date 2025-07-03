package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.TrackHistoryRepository
import com.example.playlistmaker.search.domain.Track

class PlayerInteractorImpl(
    private val trackHistoryRepository: TrackHistoryRepository,
    private val playerRepository: PlayerRepository
) :
    PlayerInteractor {
    override fun getTrackById(id: Int): Track = trackHistoryRepository.getTrackById(id)
    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        playerRepository.preparePlayer(url, onPrepared, onCompletion)
    }

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun getCurrentPosition(): Long {
        return playerRepository.getCurrentPosition()
    }

    override fun release() {
        playerRepository.release()
    }
}
