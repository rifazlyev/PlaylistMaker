package com.example.playlistmaker.media.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_1_2 : Migration(1,2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS playlist (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "    name TEXT NOT NULL,\n" +
                "    description TEXT NOT NULL DEFAULT '',\n" +
                "    coverPath TEXT,\n" +
                "    trackIds TEXT NOT NULL DEFAULT '[]',\n" +
                "    tracksCount INTEGER NOT NULL DEFAULT 0);")
    }
}
