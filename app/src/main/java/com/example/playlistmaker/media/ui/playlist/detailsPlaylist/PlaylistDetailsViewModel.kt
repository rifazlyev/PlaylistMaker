package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.mapper.toTrackUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private var playlistId: Long? = null
    private var loadJob: Job? = null
    private val playlist = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlist

    private val stateLiveData = MutableLiveData<PlaylistDetailsUiState>()
    fun observeState(): LiveData<PlaylistDetailsUiState> = stateLiveData

    private val tracksInPlaylist = MutableLiveData<List<Track>>()
    fun observeTrackInPlaylist(): LiveData<List<Track>> = tracksInPlaylist

    private val deletePlaylistResult = MutableLiveData<Int>()
    fun observeDeletePlaylist(): LiveData<Int> = deletePlaylistResult

    private val uiSharingEvent = MutableSharedFlow<PlaylistDetailsEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun observeUiSharingEvent(): SharedFlow<PlaylistDetailsEvent> = uiSharingEvent

    fun loadPlaylist(playlistId: Long) {
        this.playlistId = playlistId
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            this@PlaylistDetailsViewModel.playlist.postValue(playlist)
            val tracksIds = playlist.trackIds
            playlistInteractor.getTracksFromPlaylist(tracksIds)
                .collect {
                    processResult(it)
                }
        }
    }

    private fun render(state: PlaylistDetailsUiState) {
        stateLiveData.postValue(state)
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            render(PlaylistDetailsUiState.Empty)
            tracksInPlaylist.postValue(emptyList())
        } else {
            tracksInPlaylist.postValue(tracks)
            val tracksUI = tracks.map { it.toTrackUi() }
            render(PlaylistDetailsUiState.Content(tracksUI))
        }
    }

    fun deleteTrack(trackId: Long) {
        val id = playlistId ?: return
        viewModelScope.launch {
            playlistInteractor.deleteTrack(trackId = trackId, playlistId = id)
            loadPlaylist(id)
        }
    }

    fun deletePlaylist() {
        val id = playlistId
        if (id == null) {
            deletePlaylistResult.postValue(0)
            return
        }

        viewModelScope.launch {
            deletePlaylistResult.postValue(playlistInteractor.deletePlaylist(id))
        }
    }

    fun sharePlaylist() {
        val playlist = playlist.value ?: return
        val tracks = tracksInPlaylist.value
        if (tracks.isNullOrEmpty()) {
            uiSharingEvent.tryEmit(PlaylistDetailsEvent.ShowNoTracksToast)
        } else {
            playlistInteractor.shareApp(playlist, tracks)
        }
    }
}
