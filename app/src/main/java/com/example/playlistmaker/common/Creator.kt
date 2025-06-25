package com.example.playlistmaker.common

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.data.ThemeControllerImpl
import com.example.playlistmaker.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.ThemeController
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private val handler = Handler(Looper.getMainLooper())

    fun getHandler(): Handler = handler

    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(
            getTrackRepository(context),
            provideTrackHistory(context)
        )
    }

    fun provideTrackHistory(context: Context): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(context)
    }

    fun provideThemeController(context: Context): ThemeController {
        return ThemeControllerImpl(context)
    }

    fun providePlayerInteractor(context: Context): PlayerInteractor {
        val trackHistoryRepositoryImpl = provideTrackHistory(context)
        return PlayerInteractorImpl(trackHistoryRepositoryImpl)
    }
}
