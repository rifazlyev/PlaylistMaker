package com.example.playlistmaker.media.domain

import android.net.Uri
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
    private val fileRepository: FileRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(name: String, description: String, uri: Uri?): Long {
        if (uri == null) return playlistRepository.createPlaylist(
            Playlist(
                name = name,
                description = description,
            )
        )
        val coverPath = fileRepository.copyToPrivateStorage(uri)
        return playlistRepository.createPlaylist(
            Playlist(
                name = name,
                description = description,
                coverPath = coverPath,
            )
        )
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addTrackToPlaylistAndUpdate(
        track: Track,
        playlistId: Long
    ): Long {
        return playlistRepository.addTrackToPlaylistAndUpdate(track, playlistId)
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override fun getTracksFromPlaylist(ids: List<Long>): Flow<List<Track>> {
        if (ids.isEmpty()) return flowOf(emptyList())
        return playlistRepository.getTracksFromPlaylist(ids)
    }

    override suspend fun deleteTrack(trackId: Long, playlistId: Long) {
        playlistRepository.deleteTrack(trackId = trackId, playlistId = playlistId)
    }
}
