package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(trackEntity: TrackEntity)

    @Delete
    suspend fun deleteFavoriteTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM favorite_tracks ORDER BY addedAt ASC")
    fun getListOfFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId from favorite_tracks")
    suspend fun getFavoriteTracksId(): List<Long>
}
