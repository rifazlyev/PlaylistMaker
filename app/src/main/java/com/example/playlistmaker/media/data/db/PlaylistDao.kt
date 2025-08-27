package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity): Long

    @Query("SELECT * FROM playlist ORDER BY id DESC")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Update()
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM PLAYLIST WHERE ID = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity
}
