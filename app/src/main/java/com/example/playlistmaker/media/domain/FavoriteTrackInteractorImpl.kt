package com.example.playlistmaker.media.domain

import com.example.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTrackInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
) : FavoriteTrackInteractor {
    override suspend fun addFavoriteTrack(track: Track) {
        favoriteTrackRepository.addFavoriteTrack(track)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        favoriteTrackRepository.deleteFavoriteTrack(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackRepository.getFavoriteTracks().map {
            //делаю это тут, так как это бизнес-логика
            it.reversed()
        }
    }
}
