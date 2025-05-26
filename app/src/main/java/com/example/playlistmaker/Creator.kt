package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private fun getTrackRepository(): TrackRepository{
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor{
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideTrackHistory(sharedPreferences: SharedPreferences): TrackHistoryRepository{
        return TrackHistoryRepositoryImpl(sharedPreferences)
    }
}
