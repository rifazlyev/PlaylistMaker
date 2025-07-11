package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.Track

interface PlayerInteractor {
    fun getTrackById(id: Int): Track
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun getCurrentPosition(): Long
    fun release()
}
