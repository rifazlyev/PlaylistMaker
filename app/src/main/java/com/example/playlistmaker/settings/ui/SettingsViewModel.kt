package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themeInteractor: ThemeInteractor
) : ViewModel() {

    private val themeState = MutableLiveData(themeInteractor.isDarkThemeEnabled())
    fun observeThemeState(): LiveData<Boolean> = themeState

    fun switchTheme(isDark: Boolean){
        themeInteractor.switchTheme(isDark)
        themeState.postValue(isDark)
    }

    fun shareApp(){
        sharingInteractor.shareApp()
    }

    fun openTerms(){
        sharingInteractor.openTerms()
    }

    fun openSupport(){
        sharingInteractor.openSupport()
    }
}
