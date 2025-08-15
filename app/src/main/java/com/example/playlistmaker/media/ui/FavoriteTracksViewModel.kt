package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.mapper.toTrackUi
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {

    init {
        viewModelScope.launch {
            favoriteTrackInteractor.getFavoriteTracks().collect { favoriteTrackListFromBd ->
                processResult(favoriteTrackListFromBd)
            }
        }
    }

    private val stateLiveData = MutableLiveData<FavoriteUiState>()
    fun observeState(): LiveData<FavoriteUiState> = stateLiveData

    private fun renderState(favoriteUiState: FavoriteUiState) {
        stateLiveData.value = favoriteUiState
    }

    private fun processResult(list: List<Track>) {
        if (list.isEmpty()) {
            renderState(FavoriteUiState.Empty)
        } else {
            val listOfFavoriteTracksUi = list.map {
                it.toTrackUi()
            }
            renderState(FavoriteUiState.Content(listOfFavoriteTracksUi))
        }
    }
}
