package com.example.playlistmaker.settings.di

import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.common.PreferencesConstants.PLAYLIST_PREFERENCES
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val settingsDataModule = module {
    single{
        androidContext().getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
    }
}
