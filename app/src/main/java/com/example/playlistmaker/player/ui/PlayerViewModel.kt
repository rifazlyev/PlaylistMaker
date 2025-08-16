package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.formatTrackTime
import com.example.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.ui.mapper.toTrackDomain
import com.example.playlistmaker.search.ui.model.TrackUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {
    companion object {
        const val CUSTOM_DELAY = 300L
        const val FAVORITE_DELAY = 500L
    }

    sealed interface PlayerState {
        data object Default : PlayerState
        data object Prepared : PlayerState
        data object Playing : PlayerState
        data object Paused : PlayerState
    }

    private var isFavoriteClickAllowed: Boolean = true
    private var timerJob: Job? = null
    private var favoriteJob: Job? = null

    private val trackLiveData = MutableLiveData<TrackUi>()
    fun observeTrackLiveData(): LiveData<TrackUi> = trackLiveData

    fun initializePlayer(track: TrackUi) {
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
        favoriteJob?.cancel()
    }

    fun favoriteClickDebounce(): Boolean {
        val currentValue = isFavoriteClickAllowed
        if (isFavoriteClickAllowed) {
            isFavoriteClickAllowed = false
            viewModelScope.launch {
                delay(FAVORITE_DELAY)
                isFavoriteClickAllowed = true
            }
        }
        return currentValue
    }

    fun onFavoriteClicked() {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            val track = trackLiveData.value ?: return@launch
            val domain = track.toTrackDomain(track.isFavorite)
            if (track.isFavorite) {
                favoriteTrackInteractor.deleteFavoriteTrack(domain)
            } else {
                favoriteTrackInteractor.addFavoriteTrack(domain)
            }
            val newValue = !track.isFavorite
            trackLiveData.value = (track.copy(isFavorite = newValue))
        }
    }
}
