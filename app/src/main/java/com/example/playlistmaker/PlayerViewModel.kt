package com.example.playlistmaker

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.common.Creator
import com.example.playlistmaker.common.Creator.getHandler
import com.example.playlistmaker.common.UiUtils.formatTrackTime
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.models.Track

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {
    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val CUSTOM_DELAY = 300L

        fun getFactory(trackId: Int, context: Context): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PlayerViewModel(Creator.providePlayerInteractor(context)).apply {
                        initializePlayer(trackId)
                    }
                }
            }
    }

    var mediaPlayer: MediaPlayer = MediaPlayer()

    private val trackLiveData = MutableLiveData<Track>()
    fun observeTrackLiveData(): LiveData<Track> = trackLiveData

    fun initializePlayer(trackId: Int) {
        val track = playerInteractor.getTrackById(trackId)
        trackLiveData.postValue(track)
        preparePlayer(track.previewUrl ?: "")
    }

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData(formatTrackTime(0))
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData
    private val handler = getHandler()

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PAUSED, STATE_PREPARED -> startPlayer()
        }
    }

    private val runnableUpdateTime = object : Runnable {
        override fun run() {
            if (playerStateLiveData.value == STATE_PLAYING) {
                progressTimeLiveData.postValue(formatTrackTime(mediaPlayer.currentPosition.toLong()))
                handler.postDelayed(this, CUSTOM_DELAY)
            }
        }
    }

    private fun resetTimer() {
        handler.removeCallbacks(runnableUpdateTime)
        progressTimeLiveData.postValue(formatTrackTime(0))
    }

    private fun pauseTimer() {
        handler.removeCallbacks(runnableUpdateTime)
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        handler.post(runnableUpdateTime)
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.value = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.value = STATE_PREPARED
            resetTimer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
    }

    fun onPaused() {
        pausePlayer()
    }

    fun onDestroy() {
        mediaPlayer.release()
        pauseTimer()
    }
}
