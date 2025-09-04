package com.example.playlistmaker.media.domain

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist): Long
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylistAndUpdate(
        track: Track,
        playlistId: Long
    ): Long

    suspend fun getPlaylistById(playlistId: Long): Playlist
    fun getTracksFromPlaylist(list: List<Long>): Flow<List<Track>>
    suspend fun deleteTrack(trackId: Long, playlistId: Long)
    suspend fun deletePlaylist(playlistId: Long): Int
}
