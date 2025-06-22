package com.example.playlistmaker.ui.player

import PlayerViewModel
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
import com.example.playlistmaker.presentation.mapper.toTrackUi
import com.example.playlistmaker.presentation.model.TrackUi

class PlayerActivity : AppCompatActivity() {
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var buttonBack: ImageButton
    private lateinit var trackImage: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var trackArtistTitle: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackDurationValue: TextView
    private lateinit var albumTitle: TextView
    private lateinit var yearTitle: TextView
    private lateinit var genreTitle: TextView
    private lateinit var countryTitle: TextView
    private lateinit var albumGroupInfo: Group
    private lateinit var playButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        buttonBack = findViewById(R.id.back_button_player_screen)
        buttonBack.setOnClickListener {
            finish()
        }

        trackTitle = findViewById(R.id.player_track_title)
        trackArtistTitle = findViewById(R.id.player_artist_title)
        trackImage = findViewById(R.id.player_image_placeholder)
        trackDuration = findViewById(R.id.player_track_duration)
        trackDurationValue = findViewById(R.id.player_duration_value)
        albumTitle = findViewById(R.id.player_album_value)
        yearTitle = findViewById(R.id.player_year_value)
        genreTitle = findViewById(R.id.player_genre_value)
        countryTitle = findViewById(R.id.player_country_value)
        albumGroupInfo = findViewById(R.id.player_album_info_group)
        playButton = findViewById(R.id.player_play_button)
        albumGroupInfo.visibility = View.GONE

        val trackId = intent.getIntExtra(TRACK, -1)
        if (trackId == -1){
            Toast.makeText(this, getString(R.string.track_not_found), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        playerViewModel =
            ViewModelProvider(this, PlayerViewModel.getFactory(trackId, this)).get(
                PlayerViewModel::class.java
            )

        playerViewModel.observePlayerState().observe(this) {
            if (it == PlayerViewModel.STATE_PLAYING) {
                enabledPauseButton()
            } else {
                enablePlayButton()
            }
        }

        playerViewModel.observeProgressTime().observe(this) {
            trackDuration.text = it
        }

        playButton.setOnClickListener {
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

    private fun enablePlayButton() = playButton.setImageResource(R.drawable.ic_play)
    private fun enabledPauseButton() = playButton.setImageResource(R.drawable.ic_pause_track)

    private fun renderTrackUi(trackUi: TrackUi) {
        val radiusPx = dpToPx(8F, context = this)

        trackTitle.text = trackUi.trackName
        trackArtistTitle.text = trackUi.artistName
        trackDuration.text = trackUi.formattedTime
        trackDurationValue.text = trackUi.formattedTime
        if (trackUi.collectionName.isNotBlank()) {
            albumGroupInfo.visibility = View.VISIBLE
            albumTitle.text = trackUi.collectionName
        }
        yearTitle.text = trackUi.releaseDate
        genreTitle.text = trackUi.primaryGenreName
        countryTitle.text = trackUi.country

        Glide.with(this)
            .load(trackUi.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_player_screen)
            .centerCrop()
            .transform(RoundedCorners(radiusPx))
            .into(trackImage)
    }
}
