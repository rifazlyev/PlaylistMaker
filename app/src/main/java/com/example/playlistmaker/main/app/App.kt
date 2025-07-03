package com.example.playlistmaker.main.app

import android.app.Application
import com.example.playlistmaker.player.di.playerModule
import com.example.playlistmaker.search.di.searchModule
import com.example.playlistmaker.settings.di.settingsModule
import com.example.playlistmaker.settings.domain.ThemeInteractor
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    private lateinit var themeInteractor: ThemeInteractor
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    settingsModule,
                    searchModule,
                    playerModule
                )
            )
        }
        themeInteractor = get<ThemeInteractor>()
        val isDarkTheme = themeInteractor.isDarkThemeEnabled()
        themeInteractor.switchTheme(isDarkTheme)
    }
}
