package com.example.playlistmaker.settings.domain

import androidx.appcompat.app.AppCompatDelegate

class ThemeInteractorImpl(private val themeRepository: ThemeRepository) : ThemeInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }

    override fun switchTheme(enabled: Boolean) {
        themeRepository.switchTheme(enabled)
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
