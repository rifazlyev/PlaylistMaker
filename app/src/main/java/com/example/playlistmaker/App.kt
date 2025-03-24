package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.constants.PreferencesConstants.APP_THEME_KEY
import com.example.playlistmaker.constants.PreferencesConstants.PLAYLIST_PREFERENCES

class App : Application() {
    var darkTheme: Boolean = false
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(APP_THEME_KEY, false)
        switchTheme(darkTheme)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}