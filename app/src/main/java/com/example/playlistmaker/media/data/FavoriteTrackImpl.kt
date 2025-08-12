package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.converter.TrackDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.TrackEntity
import com.example.playlistmaker.media.domain.FavoriteTrackRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class FavoriteTrackImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteTrackRepository {
    override suspend fun addFavoriteTrack(track: Track) = withContext(Dispatchers.IO) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.getTrackDao().insertFavoriteTrack(trackEntity)
    }

    override suspend fun deleteFavoriteTrack(track: Track) = withContext(Dispatchers.IO) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.getTrackDao().deleteFavoriteTrack(trackEntity)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.getTrackDao().getListOfFavoriteTracks()
        emit(convertToTrackDomain(tracks))
    }.flowOn(Dispatchers.IO)

    private fun convertToTrackDomain(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}
