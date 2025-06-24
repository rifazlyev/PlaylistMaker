package com.example.playlistmaker

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.common.Creator
import com.example.playlistmaker.domain.api.TrackInteractor

class SearchViewModel(private val trackInteractor: TrackInteractor) : ViewModel() {
    companion object {
        fun getFactory(context: Context): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { SearchViewModel(Creator.provideTrackInteractor(context)) }
            }
    }
}
