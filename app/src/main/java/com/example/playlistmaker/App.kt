package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.common.Creator
import com.example.playlistmaker.domain.api.ThemeInteractor

class App : Application() {
    private lateinit var themeInteractor: ThemeInteractor
    override fun onCreate() {
        super.onCreate()
        themeInteractor = Creator.provideThemeInteractor(applicationContext)
        val isDarkTheme = themeInteractor.isDarkThemeEnabled()
        themeInteractor.switchTheme(isDarkTheme)
    }
}
