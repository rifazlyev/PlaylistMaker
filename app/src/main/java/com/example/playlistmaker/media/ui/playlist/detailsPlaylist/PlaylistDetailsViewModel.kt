package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.TrackInPlaylist
import com.example.playlistmaker.media.ui.mapper.toPlaylistUi
import com.example.playlistmaker.media.ui.mapper.toTrackInPlaylistUi
import com.example.playlistmaker.media.ui.model.PlaylistUi
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    init {
        viewModelScope.launch {
            playlistInteractor.getTracksFromPlaylist(playlist.value.trackIds).collect {

            }
        }
    }


    private val playlist = MutableLiveData<PlaylistUi>()
    fun observePlaylist(): LiveData<PlaylistUi> = playlist

    private val stateLiveData = MutableLiveData<PlaylistDetailsUiState>()
    fun observeState(): LiveData<PlaylistDetailsUiState> = stateLiveData

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlistUi = playlistInteractor.getPlaylistById(playlistId).toPlaylistUi()
            playlist.value = playlistUi
        }
    }

    private fun render(state: PlaylistDetailsUiState) {
        stateLiveData.value = state
    }

    private fun processResult(list: List<TrackInPlaylist>) {
        if (list.isEmpty()) {
            render(PlaylistDetailsUiState.Empty)
        } else {
            val tracks = list.map { it.toTrackInPlaylistUi() }
            render(PlaylistDetailsUiState.Content(tracks))
        }
    }
}
