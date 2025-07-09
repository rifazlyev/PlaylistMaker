package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewModel.observeThemeState().observe(viewLifecycleOwner) {
            binding.themeSwitcher.isChecked = it
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
