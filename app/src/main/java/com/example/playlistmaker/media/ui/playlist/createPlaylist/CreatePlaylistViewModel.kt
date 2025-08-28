package com.example.playlistmaker.media.ui.playlist.createPlaylist

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    fun createPlaylist(name: String, description: String, uri: Uri?) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(name = name, description = description, uri = uri)
        }
    }
}
