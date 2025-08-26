package com.example.playlistmaker.media.domain

import android.net.Uri
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.model.TrackInPlaylist
import kotlinx.coroutines.flow.Flow

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

    override suspend fun addTrackToPlaylistAndUpdate(trackInPlaylist: TrackInPlaylist, playlist: Playlist): Long {
        return playlistRepository.addTrackToPlaylistAndUpdate(trackInPlaylist,playlist)
    }
}
