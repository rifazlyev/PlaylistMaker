package com.example.playlistmaker.domain.api

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(enabled: Boolean)
}