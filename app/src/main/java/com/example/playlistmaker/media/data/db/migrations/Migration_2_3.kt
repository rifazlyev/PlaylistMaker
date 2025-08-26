package com.example.playlistmaker.media.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_2_3 : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS track_in_playlist (\n" +
                "    trackId INTEGER NOT NULL,\n" +
                "    artworkUrl100 TEXT NOT NULL,\n" +
                "    trackName TEXT NOT NULL,\n" +
                "    artistName TEXT NOT NULL,\n" +
                "    collectionName TEXT NOT NULL,\n" +
                "    releaseDate TEXT NOT NULL,\n" +
                "    primaryGenreName TEXT NOT NULL,\n" +
                "    country TEXT NOT NULL,\n" +
                "    trackTime INTEGER NOT NULL,\n" +
                "    formattedTime TEXT NOT NULL,\n" +
                "    previewUrl TEXT,\n" +
                "    addedAt INTEGER NOT NULL,\n" +
                "    PRIMARY KEY(trackId)\n" +
                ");")
    }
}
