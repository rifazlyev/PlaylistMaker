package com.example.playlistmaker.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.SettingsViewModel
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var settingsViewModel: SettingsViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingsViewModel = ViewModelProvider(this, SettingsViewModel.getFactory(this)).get(
            SettingsViewModel::class.java
        )

        settingsViewModel.observeThemeState().observe(this){
            binding.themeSwitcher.isChecked = it
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
        }

        binding.settingsScreen.setOnClickListener {
            finish()
        }

        binding.backButtonSettingsScreen.setOnClickListener {
            finish()
        }

        binding.shareApp.setOnClickListener {
            settingsViewModel.shareApp()
        }

        binding.support.setOnClickListener {
           settingsViewModel.openSupport()
        }

        binding.privacyPolicy.setOnClickListener {
            settingsViewModel.openTerms()
        }
    }
}
