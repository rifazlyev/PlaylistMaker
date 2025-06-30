package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import org.koin.dsl.module

val settingsRepositoryModule = module {
    single<ThemeRepository>{
        ThemeRepositoryImpl(get())
    }
    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }
}
