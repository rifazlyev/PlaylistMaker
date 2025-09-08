package com.example.playlistmaker.media.data

import android.content.Context
import android.content.Intent
import androidx.room.Transaction
import com.example.playlistmaker.R
import com.example.playlistmaker.common.formatTrackTime
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
    private val trackInPlaylistDbConverter: TrackInPlaylistDbConverter,
    private val context: Context
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
        if (!isTrackPresentInPlaylists(trackId)) {
            appDatabase.getTrackInPlaylistDao().deleteTrack(trackId)
        }
    }

    @Transaction
    override suspend fun deletePlaylist(playlistId: Long): Int {
        val playlist =
            playlistDbConverter.map(appDatabase.getPlaylistDao().getPlaylistById(playlistId))
        val tracksInPlaylist = playlist.trackIds
        val listOfOtherPlaylist: List<Playlist> =
            appDatabase.getPlaylistDao().getPlaylists().first().map {
                playlistDbConverter.map(it)
            }.filter { playlistId != it.id }
        val otherIds = listOfOtherPlaylist.flatMap { it.trackIds }.toSet()
        val idsToDelete = tracksInPlaylist.filter { it !in otherIds }
        idsToDelete.forEach { appDatabase.getTrackInPlaylistDao().deleteTrack(it) }
        return appDatabase.getPlaylistDao().deletePlaylist(playlistDbConverter.map(playlist))
    }

    override fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {
        val stringBuilder = StringBuilder()
        stringBuilder.appendLine(playlist.name)
        if (playlist.description.isNotBlank()) {
            stringBuilder.appendLine(playlist.description)
        }
        val tracksCountText = context.resources.getQuantityString(
            R.plurals.playlist_tracks_count,
            tracks.size,
            tracks.size
        )
        stringBuilder.appendLine(tracksCountText)
        for ((index, value) in tracks.withIndex()) {
            stringBuilder.appendLine(
                "${index + 1}. ${value.artistName} - ${value.trackName} (${
                    formatTrackTime(
                        value.trackTime
                    )
                })"
            )
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, stringBuilder.toString())
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, context.getString(R.string.choose_the_app))
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private suspend fun isTrackPresentInPlaylists(trackId: Long): Boolean {
        val listOfPlaylist: List<Playlist> = appDatabase.getPlaylistDao().getPlaylists().first()
            .map { playlistDbConverter.map(it) }
        return listOfPlaylist.any { trackId in it.trackIds }
    }
}
