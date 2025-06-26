package com.example.playlistmaker.settings.domain

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(enabled: Boolean)
}