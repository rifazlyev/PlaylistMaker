package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.ui.mapper.toPlaylistUi
import com.example.playlistmaker.media.ui.model.PlaylistUi
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.mapper.toTrackUi
import com.example.playlistmaker.search.ui.model.TrackUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private var playlistId: Long? = null
    private var loadJob: Job? = null
    private val playlist = MutableLiveData<PlaylistUi>()
    fun observePlaylist(): LiveData<PlaylistUi> = playlist

    private val stateLiveData = MutableLiveData<PlaylistDetailsUiState>()
    fun observeState(): LiveData<PlaylistDetailsUiState> = stateLiveData

    private val tracksInPlaylist = MutableLiveData<List<TrackUi>>()
    fun observeTrackInPlaylist(): LiveData<List<TrackUi>> = tracksInPlaylist

    fun loadPlaylist(playlistId: Long) {
        this.playlistId = playlistId
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val playlistUi = playlistInteractor.getPlaylistById(playlistId).toPlaylistUi()
            playlist.postValue(playlistUi)
            val tracksIds = playlistUi.trackIds
            playlistInteractor.getTracksFromPlaylist(tracksIds)
                .collect {
                    processResult(it)
                }
        }
    }

    private fun render(state: PlaylistDetailsUiState) {
        stateLiveData.postValue(state)
    }

    private fun processResult(list: List<Track>) {
        val uiTracks = list.map { it.toTrackUi() }
        if (uiTracks.isEmpty()) {
            render(PlaylistDetailsUiState.Empty)
            tracksInPlaylist.postValue(emptyList())
        } else {
            tracksInPlaylist.postValue(uiTracks)
            render(PlaylistDetailsUiState.Content(uiTracks))
        }
    }

    fun deleteTrack(trackId: Long) {
        val id = playlistId ?: return
        viewModelScope.launch {
            playlistInteractor.deleteTrack(trackId = trackId, playlistId = id)
            loadPlaylist(id)
        }
    }
}
