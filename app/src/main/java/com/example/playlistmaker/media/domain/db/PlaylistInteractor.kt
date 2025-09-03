package com.example.playlistmaker.media.domain.db

import android.net.Uri
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String, uri: Uri?): Long
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylistAndUpdate(
        track: Track, playlistId: Long
    ): Long

    suspend fun getPlaylistById(playlistId: Long): Playlist
    fun getTracksFromPlaylist(ids: List<Long>): Flow<List<Track>>
    suspend fun deleteTrack(trackId: Long, playlistId: Long)

}
