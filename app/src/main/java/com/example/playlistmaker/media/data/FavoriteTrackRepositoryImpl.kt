package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.converter.TrackDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.domain.FavoriteTrackRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoriteTrackRepositoryImpl(
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

    override fun getFavoriteTracks(): Flow<List<Track>> =
        appDatabase.getTrackDao().getListOfFavoriteTracks().map { entities ->
            entities.map { trackDbConvertor.map(it) }
        }
}
