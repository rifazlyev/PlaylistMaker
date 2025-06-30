package com.example.playlistmaker.common

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.common.PreferencesConstants.PLAYLIST_PREFERENCES
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.settings.domain.ThemeInteractorImpl
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.search.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.example.playlistmaker.search.domain.TrackHistoryRepository
import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import com.example.playlistmaker.search.domain.TrackInteractorImpl

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

    private fun getThemeRepository(context: Context): ThemeRepository =
        ThemeRepositoryImpl(getSharedPreferences(context))

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }

    fun providePlayerInteractor(context: Context): PlayerInteractor {
        val trackHistoryRepositoryImpl = provideTrackHistory(context)
        return PlayerInteractorImpl(trackHistoryRepositoryImpl)
    }

    fun getExternalNavigator(context: Context): ExternalNavigator = ExternalNavigatorImpl(context)
    fun provideSharingInteractor(context: Context): SharingInteractor =
        SharingInteractorImpl(getExternalNavigator(context))
}
