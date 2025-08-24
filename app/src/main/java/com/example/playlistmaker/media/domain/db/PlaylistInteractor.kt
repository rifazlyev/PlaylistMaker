package com.example.playlistmaker.media.domain.db

import android.net.Uri
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String, uri: Uri?): Long
    fun getPlaylists(): Flow<List<Playlist>>
}
