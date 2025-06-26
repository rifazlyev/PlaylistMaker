package com.example.playlistmaker.ui.player

import com.example.playlistmaker.PlayerViewModel
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.IntentKeys.TRACK
import com.example.playlistmaker.common.UiUtils.dpToPx
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.presentation.mapper.toTrackUi
import com.example.playlistmaker.presentation.model.TrackUi

class PlayerActivity : AppCompatActivity() {
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButtonPlayerScreen.setOnClickListener {
            finish()
        }

        binding.playerAlbumInfoGroup.visibility = View.GONE

        val trackId = intent.getIntExtra(TRACK, -1)
        if (trackId == -1) {
            Toast.makeText(this, getString(R.string.track_not_found), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        playerViewModel =
            ViewModelProvider(this, PlayerViewModel.getFactory(trackId, this)).get(
                PlayerViewModel::class.java
            )

        playerViewModel.observePlayerState().observe(this) {
            if (it == PlayerViewModel.PlayerState.Playing) {
                enabledPauseButton()
            } else {
                enablePlayButton()
            }
        }

        playerViewModel.observeProgressTime().observe(this) {
            binding.playerTrackDuration.text = it
        }


        binding.playerPlayButton.setOnClickListener {
            playerViewModel.onPlayButtonClicked()
        }

        playerViewModel.observeTrackLiveData().observe(this) { trackDomain ->
            val ui = trackDomain.toTrackUi()
            renderTrackUi(ui)
        }
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.onPaused()
        enablePlayButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.onDestroy()
    }

    private fun enablePlayButton() = binding.playerPlayButton.setImageResource(R.drawable.ic_play)
    private fun enabledPauseButton() =
        binding.playerPlayButton.setImageResource(R.drawable.ic_pause_track)

    private fun renderTrackUi(trackUi: TrackUi) {
        val radiusPx = dpToPx(8F, context = this)

        binding.playerTrackTitle.text = trackUi.trackName
        binding.playerArtistTitle.text = trackUi.artistName
        binding.playerTrackDuration.text = trackUi.formattedTime
        binding.playerDurationValue.text = trackUi.formattedTime

        if (trackUi.collectionName.isNotBlank()) {
            binding.playerAlbumInfoGroup.visibility = View.VISIBLE
            binding.playerAlbumValue.text = trackUi.collectionName

        }
        binding.playerYearValue.text = trackUi.releaseDate
        binding.playerGenreValue.text = trackUi.primaryGenreName
        binding.playerCountryValue.text = trackUi.country

        Glide.with(this)
            .load(trackUi.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_player_screen)
            .centerCrop()
            .transform(RoundedCorners(radiusPx))
            .into(binding.playerImagePlaceholder)
    }
}
