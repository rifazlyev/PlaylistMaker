package com.example.playlistmaker.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var settingsViewModel: SettingsViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingsViewModel = ViewModelProvider(this, SettingsViewModel.getFactory(this)).get(
            SettingsViewModel::class.java
        )

        themeSwitcher = findViewById(R.id.themeSwitcher)

        settingsViewModel.observeThemeState().observe(this){
            themeSwitcher.isChecked = it
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
        }

        //Тут сделал кликабельным весь контейнер
        val backSettings = findViewById<LinearLayout>(R.id.settings_screen)
        backSettings.setOnClickListener {
            finish()
        }

        //Тут кликабельна и сама 'стрелочка'
        val buttonBack = findViewById<ImageButton>(R.id.back_button_settings_screen)
        buttonBack.setOnClickListener {
            backSettings.performClick()
        }

        val buttonShareApp = findViewById<LinearLayout>(R.id.share_app)
        buttonShareApp.setOnClickListener {
            settingsViewModel.shareApp()
        }

        val buttonSupport = findViewById<LinearLayout>(R.id.support)
        buttonSupport.setOnClickListener {
           settingsViewModel.openSupport()
        }

        val buttonPrivacyPolicy = findViewById<LinearLayout>(R.id.privacy_policy)
        buttonPrivacyPolicy.setOnClickListener {
            settingsViewModel.openTerms()
        }
    }
}
