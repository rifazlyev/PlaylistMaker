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
import com.example.playlistmaker.media.ui.model.TrackInPlaylistUi
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val playlist = MutableLiveData<PlaylistUi>()
    fun observePlaylist(): LiveData<PlaylistUi> = playlist

    private val stateLiveData = MutableLiveData<PlaylistDetailsUiState>()
    fun observeState(): LiveData<PlaylistDetailsUiState> = stateLiveData

    private val tracksInPlaylist = MutableLiveData<List<TrackInPlaylistUi>>()
    fun observeTrackInPlaylist(): LiveData<List<TrackInPlaylistUi>> = tracksInPlaylist

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlistUi = playlistInteractor.getPlaylistById(playlistId).toPlaylistUi()
            playlist.value = playlistUi
            val tracksIds = playlistUi.trackIds
            playlistInteractor.getTracksFromPlaylist(tracksIds)
                .collect {
                    processResult(it)
                }
        }
    }

    private fun render(state: PlaylistDetailsUiState) {
        stateLiveData.value = state
    }

    private fun processResult(list: List<TrackInPlaylist>) {
        val uiTracks = list.map { it.toTrackInPlaylistUi() }
        tracksInPlaylist.value = uiTracks
        render(PlaylistDetailsUiState.Content(uiTracks))
    }
}
