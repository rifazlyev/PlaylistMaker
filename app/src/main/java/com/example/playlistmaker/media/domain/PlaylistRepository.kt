package com.example.playlistmaker.media.domain

import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist): Long
    fun getPlaylists(): Flow<List<Playlist>>
}
