package com.example.playlistmaker.main.app

import android.app.Application
import com.example.playlistmaker.common.Creator
import com.example.playlistmaker.settings.di.dataModule
import com.example.playlistmaker.settings.di.interactorModule
import com.example.playlistmaker.settings.di.repositoryModule
import com.example.playlistmaker.settings.di.viewModelModule
import com.example.playlistmaker.settings.domain.ThemeInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    private lateinit var themeInteractor: ThemeInteractor
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
        themeInteractor = Creator.provideThemeInteractor(applicationContext)
        val isDarkTheme = themeInteractor.isDarkThemeEnabled()
        themeInteractor.switchTheme(isDarkTheme)
    }
}
