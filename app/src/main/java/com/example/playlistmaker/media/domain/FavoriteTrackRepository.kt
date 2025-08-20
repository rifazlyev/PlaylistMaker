package com.example.playlistmaker.media.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackRepository {
    suspend fun addFavoriteTrack(track: Track)
    suspend fun deleteFavoriteTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
}
