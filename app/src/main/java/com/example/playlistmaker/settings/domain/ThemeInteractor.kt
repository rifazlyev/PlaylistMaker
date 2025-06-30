package com.example.playlistmaker.settings.domain

interface ThemeInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(enabled: Boolean)
}
