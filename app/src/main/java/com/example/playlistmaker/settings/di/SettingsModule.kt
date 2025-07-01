package com.example.playlistmaker.settings.di

import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.common.PreferencesConstants.PLAYLIST_PREFERENCES
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val settingsModule = module {
    single {
        androidContext().getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
    }
    single<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }
    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }
    factory<ThemeInteractor> {
        ThemeInteractorImpl(get())
    }
}
