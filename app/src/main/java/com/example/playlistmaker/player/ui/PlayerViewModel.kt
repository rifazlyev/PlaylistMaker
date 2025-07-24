package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.UiUtils.formatTrackTime
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    private var timerJob: Job? = null

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

    private fun resetTimer() {
        progressTimeLiveData.postValue(formatTrackTime(0))
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun pausePlayer() {
        pauseTimer()
        playerInteractor.pausePlayer()
        playerStateLiveData.postValue(PlayerState.Paused)
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateLiveData.value = PlayerState.Playing
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerStateLiveData.value == PlayerState.Playing) {
                delay(CUSTOM_DELAY)
                progressTimeLiveData.postValue(formatTrackTime(playerInteractor.getCurrentPosition()))
            }
        }
    }

    private fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url = url,
            onPrepared = {
                playerStateLiveData.value = PlayerState.Prepared
            },
            onCompletion = {
                playerStateLiveData.value = PlayerState.Prepared
                pausePlayer()
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
