package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.media.data.db.entity.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackToPlaylist(trackInPlaylistEntity: TrackInPlaylistEntity): Long
}
