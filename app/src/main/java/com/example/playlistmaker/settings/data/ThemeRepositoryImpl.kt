package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.common.PreferencesConstants.APP_THEME_KEY
import com.example.playlistmaker.settings.domain.ThemeRepository

class ThemeRepositoryImpl(private val sharedPreferences: SharedPreferences): ThemeRepository {

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(APP_THEME_KEY,false)

    }

    override fun switchTheme(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(APP_THEME_KEY, enabled) }
    }
}
