package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.domain.TrackInteractorImpl
import org.koin.dsl.module

val searchInteractorModule = module {
    factory<TrackInteractor> {
        TrackInteractorImpl(get(), get())
    }
}
