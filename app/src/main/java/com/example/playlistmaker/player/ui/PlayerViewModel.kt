package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.common.UiUtils.formatTrackTime
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.Track

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val handler: Handler,
) : ViewModel() {
    companion object {
        const val CUSTOM_DELAY = 300L
    }

    sealed interface PlayerState {
        data object Default : PlayerState
        data object Prepared : PlayerState
        data object Playing : PlayerState
        data object Paused : PlayerState
    }

    var mediaPlayer: MediaPlayer = MediaPlayer()

    private val trackLiveData = MutableLiveData<Track>()
    fun observeTrackLiveData(): LiveData<Track> = trackLiveData

    fun initializePlayer(trackId: Int) {
        val track = playerInteractor.getTrackById(trackId)
        trackLiveData.postValue(track)
        preparePlayer(track.previewUrl ?: "")
    }

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData(formatTrackTime(0))
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            PlayerState.Playing -> pausePlayer()
            PlayerState.Paused, PlayerState.Prepared -> startPlayer()
            //Состояне дефолт, ничего неделаем
            else -> {}
        }
    }

    private val runnableUpdateTime = object : Runnable {
        override fun run() {
            if (playerStateLiveData.value == PlayerState.Playing) {
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
        playerStateLiveData.postValue(PlayerState.Paused)
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(PlayerState.Playing)
        handler.post(runnableUpdateTime)
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.value = PlayerState.Prepared
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.value = PlayerState.Prepared
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
