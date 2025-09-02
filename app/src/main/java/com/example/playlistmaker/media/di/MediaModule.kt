package com.example.playlistmaker.media.di

import androidx.room.Room
import com.example.playlistmaker.media.data.FavoriteTrackRepositoryImpl
import com.example.playlistmaker.media.data.FileRepositoryImpl
import com.example.playlistmaker.media.data.PlaylistRepositoryImpl
import com.example.playlistmaker.media.data.converter.PlaylistDbConverter
import com.example.playlistmaker.media.data.converter.TrackDbConvertor
import com.example.playlistmaker.media.data.converter.TrackInPlaylistDbConverter
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.domain.FavoriteTrackInteractorImpl
import com.example.playlistmaker.media.domain.FavoriteTrackRepository
import com.example.playlistmaker.media.domain.FileRepository
import com.example.playlistmaker.media.domain.PlaylistInteractorImpl
import com.example.playlistmaker.media.domain.PlaylistRepository
import com.example.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.ui.favoriteTracks.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.playlist.PlaylistsViewModel
import com.example.playlistmaker.media.ui.playlist.createPlaylist.CreatePlaylistViewModel
import com.example.playlistmaker.media.ui.playlist.detailsPlaylist.PlaylistDetailsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel {
        PlaylistsViewModel(get())
    }
    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        CreatePlaylistViewModel(get())
    }

    viewModel {
        PlaylistDetailsViewModel(get())
    }
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "data.db")
            .build()
    }
    factory { TrackDbConvertor() }

    single<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(get(), get())
    }

    single<FavoriteTrackInteractor> {
        FavoriteTrackInteractorImpl(get())
    }

    factory { PlaylistDbConverter(get()) }

    factory { TrackInPlaylistDbConverter() }

    single<PlaylistRepository> { PlaylistRepositoryImpl(get(), get(), get()) }
    single<FileRepository> { FileRepositoryImpl(get()) }

    single<PlaylistInteractor> { PlaylistInteractorImpl(get(), get()) }
}
