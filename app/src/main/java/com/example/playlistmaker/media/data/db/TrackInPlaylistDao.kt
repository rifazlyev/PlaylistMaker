package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.TrackInPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackToPlaylist(trackInPlaylistEntity: TrackInPlaylistEntity): Long

    @Query("SELECT * FROM TRACK_IN_PLAYLIST")
    fun getAllTracks(): Flow<List<TrackInPlaylistEntity>>

    @Query("DELETE FROM TRACK_IN_PLAYLIST WHERE trackId =:id")
    suspend fun deleteTrack(id: Long)
}
