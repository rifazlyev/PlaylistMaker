package com.example.playlistmaker.ui.player

import PlayerViewModel
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
import com.example.playlistmaker.presentation.model.TrackUi

class PlayerActivity : AppCompatActivity() {

    private var track: TrackUi? = null
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
        trackDuration = findViewById(R.id.player_track_duration)
        trackDurationValue = findViewById(R.id.player_duration_value)
        albumTitle = findViewById(R.id.player_album_value)
        yearTitle = findViewById(R.id.player_year_value)
        genreTitle = findViewById(R.id.player_genre_value)
        countryTitle = findViewById(R.id.player_country_value)
        albumGroupInfo = findViewById(R.id.player_album_info_group)
        playButton = findViewById(R.id.player_play_button)
        albumGroupInfo.visibility = View.GONE

        track = getTrack()

        playerViewModel =
            ViewModelProvider(this, PlayerViewModel.getFactory(track?.previewUrl)).get(
                PlayerViewModel::class.java
            )

        playerViewModel.observePlayerState().observe(this) {
            if (it == PlayerViewModel.STATE_PLAYING) {
                enabledButtonPause()
            } else {
                enableButtonPlay()
            }
        }

        playerViewModel.observeProgressTime().observe(this){
            trackDuration.text = it
        }

        playButton.setOnClickListener {
            playerViewModel.onPlayButtonClicked()

        }

        val radiusPx = dpToPx(8F, context = this)
        track?.let {
            trackTitle.text = it.trackName
            trackArtistTitle.text = it.artistName
            trackDuration.text = it.formattedTime
            trackDurationValue.text = it.formattedTime
            if (it.collectionName.isNotBlank()) {
                albumGroupInfo.visibility = View.VISIBLE
                albumTitle.text = it.collectionName
            }
            yearTitle.text = it.releaseDate
            genreTitle.text = it.primaryGenreName
            countryTitle.text = it.country
        }

        trackImage = findViewById(R.id.player_image_placeholder)
        Glide.with(this)
            .load(track?.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_player_screen)
            .centerCrop()
            .transform(RoundedCorners(radiusPx))
            .into(trackImage)
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.onPaused()
        enableButtonPlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.onDestroy()
    }

    private fun getTrack(): TrackUi? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK, TrackUi::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK)
        }
    }
    private fun enableButtonPlay() = playButton.setImageResource(R.drawable.ic_play)
    private fun enabledButtonPause() = playButton.setImageResource(R.drawable.ic_pause_track)
}
