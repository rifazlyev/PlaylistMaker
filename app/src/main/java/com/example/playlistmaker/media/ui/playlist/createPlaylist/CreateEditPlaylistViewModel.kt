package com.example.playlistmaker.media.ui.playlist.createPlaylist

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.mapper.toPlaylistUi
import kotlinx.coroutines.launch

class CreateEditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private var editing: Playlist? = null

    private val playlistConditionUiState =
        MutableLiveData<PlaylistConditionUiState>(PlaylistConditionUiState.Creation)

    fun observePlaylistConditionUiState(): LiveData<PlaylistConditionUiState> =
        playlistConditionUiState

    private fun render(playlistConditionUiState: PlaylistConditionUiState) {
        this.playlistConditionUiState.value = playlistConditionUiState
    }

    fun setModeFrom(playlistId: Long?) {
        when (playlistId) {
            null -> render(PlaylistConditionUiState.Creation)
            else -> {
                viewModelScope.launch {
                    val playlist: Playlist =
                        playlistInteractor.getPlaylistById(playlistId)
                    editing = playlist
                    render(PlaylistConditionUiState.Editing(playlist.toPlaylistUi()))
                }
            }
        }
    }

    fun createPlaylist(name: String, description: String, uri: Uri?) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(name = name, description = description, uri = uri)
        }
    }

    fun editPlaylist(name: String, description: String, uri: Uri?) {
        val base = editing ?: return
        viewModelScope.launch {
            val newName = name.trim()
            val newDescription = description.trim()
            val coverPath =
                if (uri == null) base.coverPath else playlistInteractor.getCoverPath(uri)
            val updatedPlaylist =
                base.copy(name = newName, description = newDescription, coverPath = coverPath)
            playlistInteractor.editPlaylist(updatedPlaylist)
        }
    }
}
