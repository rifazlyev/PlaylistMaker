package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {
    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletion()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer.currentPosition.toLong()
    }

    override fun release() {
        mediaPlayer.release()
    }
}
