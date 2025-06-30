package com.example.playlistmaker.main.app

import android.app.Application
import com.example.playlistmaker.player.di.playerInteractorModule
import com.example.playlistmaker.player.di.playerViewModelModule
import com.example.playlistmaker.search.di.searchDataModule
import com.example.playlistmaker.search.di.searchInteractorModule
import com.example.playlistmaker.search.di.searchRepositoryModule
import com.example.playlistmaker.search.di.searchViewModelModule
import com.example.playlistmaker.settings.di.settingsDataModule
import com.example.playlistmaker.settings.di.settingsInteractorModule
import com.example.playlistmaker.settings.di.settingsRepositoryModule
import com.example.playlistmaker.settings.di.settingsViewModelModule
import com.example.playlistmaker.settings.domain.ThemeInteractor
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    private lateinit var themeInteractor: ThemeInteractor
    private val settingsModules = listOf(
        settingsDataModule,
        settingsRepositoryModule,
        settingsInteractorModule,
        settingsViewModelModule
    )

    private val searchModules = listOf(
        searchDataModule,
        searchRepositoryModule,
        searchInteractorModule,
        searchViewModelModule
    )

    private val playerModules = listOf(
        playerInteractorModule,
        playerViewModelModule
    )

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                settingsModules + searchModules + playerModules
            )
        }
        themeInteractor = get<ThemeInteractor>()
        val isDarkTheme = themeInteractor.isDarkThemeEnabled()
        themeInteractor.switchTheme(isDarkTheme)
    }
}
