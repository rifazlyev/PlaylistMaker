package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.TrackHistoryRepository
import com.example.playlistmaker.search.domain.TrackRepository
import org.koin.dsl.module

val searchRepositoryModule = module {
    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    single<TrackHistoryRepository> {
        TrackHistoryRepositoryImpl(get(), get())
    }
}
