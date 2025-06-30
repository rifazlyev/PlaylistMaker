package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }
    single<ThemeInteractor> {
        ThemeInteractorImpl(get())
    }
}
