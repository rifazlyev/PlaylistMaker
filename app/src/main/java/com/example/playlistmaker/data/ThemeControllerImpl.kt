package com.example.playlistmaker.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.common.PreferencesConstants.APP_THEME_KEY
import com.example.playlistmaker.common.PreferencesConstants.PLAYLIST_PREFERENCES
import com.example.playlistmaker.domain.ThemeController

class ThemeControllerImpl(context: Context) : ThemeController {
    private val sharedPreferences = context.getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
    private var darkThemeIsEnabled: Boolean = sharedPreferences.getBoolean(APP_THEME_KEY, false)

    override fun isDarkThemeEnabled(): Boolean {
        return darkThemeIsEnabled
    }

    override fun switchTheme(enabled: Boolean) {
        darkThemeIsEnabled = enabled
        sharedPreferences.edit()
            .putBoolean(APP_THEME_KEY, darkThemeIsEnabled)
            .apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeIsEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
