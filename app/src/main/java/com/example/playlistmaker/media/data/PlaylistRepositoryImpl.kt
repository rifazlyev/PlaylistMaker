package com.example.playlistmaker.media.data

import androidx.room.Transaction
import com.example.playlistmaker.media.data.converter.PlaylistDbConverter
import com.example.playlistmaker.media.data.converter.TrackInPlaylistDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.domain.PlaylistRepository
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    @Transaction
    override suspend fun addTrackToPlaylistAndUpdate(
        track: Track,
        playlistId: Long
    ): Long {
        val result = appDatabase.getTrackInPlaylistDao().insertTrackToPlaylist(
            trackInPlaylistDbConverter.map(track)
        )
        val playlist =
            playlistDbConverter.map(appDatabase.getPlaylistDao().getPlaylistById(playlistId))
        val newTrackIds = playlist.trackIds + track.trackId
        val updatePlaylist = playlist.copy(
            trackIds = newTrackIds,
            tracksCount = newTrackIds.size
        )
        appDatabase.getPlaylistDao().updatePlaylist(playlistDbConverter.map(updatePlaylist))
        return result
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist {
        return playlistDbConverter.map(appDatabase.getPlaylistDao().getPlaylistById(playlistId))
    }

    override fun getTracksFromPlaylist(list: List<Long>): Flow<List<Track>> {
        return appDatabase.getTrackInPlaylistDao().getAllTracks()
            .map { tracks ->
                tracks
                    .filter { list.contains(it.trackId) }
                    .sortedByDescending { it.addedAt }
                    .map { trackInPlaylistDbConverter.map(it) }
            }
    }

    @Transaction
    override suspend fun deleteTrack(trackId: Long, playlistId: Long) {
        val playlist =
            playlistDbConverter.map(appDatabase.getPlaylistDao().getPlaylistById(playlistId))
        val newTrackIds = playlist.trackIds - trackId
        val updatedPlaylist = playlist.copy(
            trackIds = newTrackIds,
            tracksCount = newTrackIds.size
        )
        appDatabase.getPlaylistDao().updatePlaylist(playlistDbConverter.map(updatedPlaylist))
        if (!isTrackPresentInPlaylists(trackId)){
            appDatabase.getTrackInPlaylistDao().deleteTrack(trackId)
        }
    }

    private suspend fun isTrackPresentInPlaylists(trackId: Long): Boolean {
        val listOfPlaylist: List<Playlist> = appDatabase.getPlaylistDao().getPlaylists().first()
            .map { playlistDbConverter.map(it) }
        return listOfPlaylist.any { trackId in it.trackIds }
    }
}
