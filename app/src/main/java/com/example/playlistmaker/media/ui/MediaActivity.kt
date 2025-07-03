package com.example.playlistmaker.media.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = PagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout,binding.viewPager){
            tab, position ->
            when(position){
                0-> tab.text = getString(R.string.favorite_tracks)
                1-> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()

        binding.backGroupMediaScreen.setOnClickListener {
            finish()
        }
        binding.backButtonMediaScreen.setOnClickListener {
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}
