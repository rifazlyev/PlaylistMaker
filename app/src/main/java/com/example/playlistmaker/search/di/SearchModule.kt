package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.TrackHistoryRepository
import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.domain.TrackInteractorImpl
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.search.ui.SearchViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {
    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }

    factory { Gson() }
    single<TrackRepository> {
        TrackRepositoryImpl(get(), get())
    }

    single<TrackHistoryRepository> {
        TrackHistoryRepositoryImpl(get(), get(), get())
    }
    factory<TrackInteractor> {
        TrackInteractorImpl(get(), get())
    }
    viewModel {
        SearchViewModel(get())
    }
}
