package com.example.playlistmaker.domain.api

interface AudioPlayer {
     fun preparePlayer(
        url: String?,
        onCompletion: () -> Unit,
        onError: () -> Unit
    )
    fun setActionOnTimeUpdate(callback: (Long) -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun playbackControl(onPlay: () -> Unit, onPause: () -> Unit)
}
