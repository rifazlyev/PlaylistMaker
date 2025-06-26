package com.example.playlistmaker.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.common.PreferencesConstants.APP_THEME_KEY
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(private val sharedPreferences: SharedPreferences): ThemeRepository  {

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(APP_THEME_KEY,false)

    }

    override fun switchTheme(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(APP_THEME_KEY, enabled) }
    }
}
