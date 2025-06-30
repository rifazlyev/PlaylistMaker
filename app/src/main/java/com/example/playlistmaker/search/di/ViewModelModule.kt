package com.example.playlistmaker.search.di

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.search.ui.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchViewModelModule = module {
    single<Handler>{
        Handler(Looper.getMainLooper())
    }

    viewModel {
        SearchViewModel(get(), get())
    }
}
