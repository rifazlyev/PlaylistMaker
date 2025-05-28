package com.example.playlistmaker.domain

interface ThemeController {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(enabled: Boolean)
}
