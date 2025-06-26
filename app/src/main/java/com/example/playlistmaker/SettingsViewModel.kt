package com.example.playlistmaker

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.common.Creator
import com.example.playlistmaker.domain.api.SharingInteractor
import com.example.playlistmaker.domain.api.ThemeInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themeInteractor: ThemeInteractor
) : ViewModel() {
    companion object {
        fun getFactory(context: Context): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SettingsViewModel(
                        Creator.provideSharingInteractor(context),
                        Creator.provideThemeInteractor(context)
                    )
                }
            }
    }

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