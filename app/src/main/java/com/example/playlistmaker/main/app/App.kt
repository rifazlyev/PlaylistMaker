package com.example.playlistmaker.main.app

import android.app.Application
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

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                settingsModules,

                )

        }
        themeInteractor = get<ThemeInteractor>()
        val isDarkTheme = themeInteractor.isDarkThemeEnabled()
        themeInteractor.switchTheme(isDarkTheme)
    }
}
