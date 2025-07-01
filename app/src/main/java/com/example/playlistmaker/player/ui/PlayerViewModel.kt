package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.common.UiUtils.formatTrackTime
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.Track

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
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

    private val handler = Handler(Looper.getMainLooper())

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
                progressTimeLiveData.postValue(formatTrackTime(playerInteractor.getCurrentPosition()))
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
        playerInteractor.pausePlayer()
        playerStateLiveData.postValue(PlayerState.Paused)
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateLiveData.postValue(PlayerState.Playing)
        handler.post(runnableUpdateTime)
    }

    private fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url = url,
            onPrepared = {
                playerStateLiveData.value = PlayerState.Prepared
            },
            onCompletion = {
                playerStateLiveData.value = PlayerState.Prepared
                resetTimer()
            })
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
        resetTimer()
    }

    fun onPaused() {
        pausePlayer()
    }

    fun onDestroy() {
        playerInteractor.release()
        pauseTimer()
    }
}
