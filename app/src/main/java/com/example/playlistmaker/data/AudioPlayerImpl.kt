package com.example.playlistmaker.data

import android.media.MediaPlayer
import android.os.Handler
import com.example.playlistmaker.domain.api.AudioPlayer

class AudioPlayerImpl(val handler: Handler) : AudioPlayer {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val CUSTOM_DELAY = 300L
    }

    private var playerState = STATE_DEFAULT
    private val mediaPlayer = MediaPlayer()
    private var actionOnTimeUpdate: ((Long) -> Unit)? = null

    override fun setActionOnTimeUpdate(callback: (Long) -> Unit) {
        actionOnTimeUpdate = callback
    }

    private val updateTime = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                val currentPosition = mediaPlayer.currentPosition.toLong()
                actionOnTimeUpdate?.invoke(currentPosition)
                handler.postDelayed(this, CUSTOM_DELAY)
            }
        }
    }

    override fun preparePlayer(
        url: String?,
        onCompletion: () -> Unit,
        onError: () -> Unit
    ) {

        if (url.isNullOrEmpty()) {
            onError()
            return
        }
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            handler.post(updateTime)
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(updateTime)
            onCompletion()
        }
        playerState = STATE_PREPARED
    }

    override fun startPlayer() {
        mediaPlayer.start()
        handler.post(updateTime)
        playerState = STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        handler.removeCallbacks(updateTime)
        playerState = STATE_PAUSED
    }

    override fun playbackControl(onPlay: () -> Unit, onPause: () -> Unit) {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                onPause()

            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                onPlay()
            }
        }
    }

    private fun release() {
        mediaPlayer.release()
    }

    fun destroy(){
        handler.removeCallbacks(updateTime)
        release()
    }
}
