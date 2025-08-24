package com.example.playlistmaker.media.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.mapper.toPlaylistUi
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    init {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlist ->
                processResult(playlist)
            }
        }
    }

    private val stateLiveData = MutableLiveData<PlaylistUiState>()
    fun observeState(): LiveData<PlaylistUiState> = stateLiveData

    private fun render(playlistUiState: PlaylistUiState) {
        stateLiveData.value = playlistUiState
    }

    private fun processResult(list: List<Playlist>) {
        if (list.isEmpty()) {
            render(PlaylistUiState.Empty)
        } else {
            val playlists = list.map { it.toPlaylistUi() }
            render(PlaylistUiState.Content(playlists))
        }
    }
}
