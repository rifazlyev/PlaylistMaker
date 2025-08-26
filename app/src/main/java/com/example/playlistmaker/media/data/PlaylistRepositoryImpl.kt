package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.converter.PlaylistDbConverter
import com.example.playlistmaker.media.data.converter.TrackInPlaylistDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.PlaylistRepository
import com.example.playlistmaker.media.domain.model.TrackInPlaylist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackInPlaylistDbConverter: TrackInPlaylistDbConverter
) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist): Long {
        return appDatabase.getPlaylistDao().insertPlaylist(
            playlistDbConverter.map(playlist)
        )
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.getPlaylistDao().getPlaylists().map { entities ->
            entities.map { playlistDbConverter.map(it) }
        }
    }

    override suspend fun addTrackToPlaylist(trackInPlaylist: TrackInPlaylist): Long {
        return appDatabase.getTrackInPlaylistDao().insertTrackToPlaylist(
            trackInPlaylistDbConverter.map(trackInPlaylist))
    }
}
