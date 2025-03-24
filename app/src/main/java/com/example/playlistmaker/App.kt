package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.constants.PreferencesConstants.APP_THEME_KEY
import com.example.playlistmaker.constants.PreferencesConstants.PLAYLIST_PREFERENCES

class App : Application() {
    private var darkTheme: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(APP_THEME_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPreferences.edit()
            .putBoolean(APP_THEME_KEY, darkThemeEnabled)
            .apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun isDarkThemeEnabled():Boolean{
        return darkTheme
    }
}