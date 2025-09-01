package com.example.playlistmaker.media.domain

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.model.TrackInPlaylist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist): Long
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylistAndUpdate(
        trackInPlaylist: TrackInPlaylist,
        playlistId: Long
    ): Long

    suspend fun getPlaylistById(playlistId: Long): Playlist
    fun getTracksFromPlaylist(list: List<Long>): Flow<List<TrackInPlaylist>>
}
