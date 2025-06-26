package com.example.playlistmaker.common

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.common.PreferencesConstants.PLAYLIST_PREFERENCES
import com.example.playlistmaker.data.ThemeInteractorImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import kotlinx.serialization.EncodeDefault

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

    private fun provideTrackHistory(context: Context): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(context)
    }

    private fun getSharedPreferences(context: Context) =
        context.getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)

    private fun provideThemeRepository(context: Context): ThemeRepository =
        ThemeRepositoryImpl(getSharedPreferences(context))

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(provideThemeRepository(context))
    }

    fun providePlayerInteractor(context: Context): PlayerInteractor {
        val trackHistoryRepositoryImpl = provideTrackHistory(context)
        return PlayerInteractorImpl(trackHistoryRepositoryImpl)
    }


}
