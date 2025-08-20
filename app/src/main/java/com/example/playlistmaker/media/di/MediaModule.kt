package com.example.playlistmaker.media.di

import androidx.room.Room
import com.example.playlistmaker.media.data.FavoriteTrackRepositoryImpl
import com.example.playlistmaker.media.data.converter.TrackDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.domain.FavoriteTrackInteractorImpl
import com.example.playlistmaker.media.domain.FavoriteTrackRepository
import com.example.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.media.ui.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel {
        PlaylistsViewModel()
    }
    viewModel {
        FavoriteTracksViewModel(get())
    }
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
    factory { TrackDbConvertor() }

    single<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(get(), get())
    }

    single<FavoriteTrackInteractor> {
        FavoriteTrackInteractorImpl(get())
    }
}
