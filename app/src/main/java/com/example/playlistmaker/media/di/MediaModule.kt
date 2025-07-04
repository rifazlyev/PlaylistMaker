package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.ui.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel {
        PlaylistsViewModel()
    }
    viewModel {
        FavoriteTracksViewModel()
    }
}
