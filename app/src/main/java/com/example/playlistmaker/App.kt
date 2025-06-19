package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.data.ThemeControllerImpl

class App : Application() {
    private lateinit var themeController : ThemeControllerImpl
    private var darkTheme: Boolean = false

    override fun onCreate() {
        super.onCreate()
        themeController = ThemeControllerImpl(this)
        darkTheme = themeController.isDarkThemeEnabled()
        themeController.switchTheme(darkTheme)
    }
}
