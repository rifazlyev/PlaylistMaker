package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.constants.IntentKeys.TRACK
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.uiUtils.UiUtils.dpToPx
import com.example.playlistmaker.uiUtils.UiUtils.formatTrackTime

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY_FOR_TOAST = 300L
    }

    private var playerState = STATE_DEFAULT
    private var track: Track? = null
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
    private var handler: Handler? = null
    private val mediaPlayer = MediaPlayer()

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
        handler = Handler(Looper.getMainLooper())
        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
        }

        val radiusPx = dpToPx(8F, context = this)
        track?.let {
            trackTitle.text = it.trackName
            trackArtistTitle.text = it.artistName
            trackDuration.text = formatTrackTime(it.trackTime)
            trackDurationValue.text = formatTrackTime(it.trackTime)
            if (it.collectionName.isNotBlank()) {
                albumGroupInfo.visibility = View.VISIBLE
                albumTitle.text = it.collectionName
            }
            yearTitle.text = it.releaseDate.take(4)
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
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun getTrack(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK)
        }
    }

    private fun preparePlayer() {
        val dataSource = track?.previewUrl
        if (dataSource.isNullOrEmpty()) {
            playButton.isEnabled = false
            handler?.postDelayed({
                Toast.makeText(this, getString(R.string.audio_error), Toast.LENGTH_SHORT).show()
            }, DELAY_FOR_TOAST)
            return
        }
        mediaPlayer.setDataSource(dataSource)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.ic_play)
        }
        playerState = STATE_PREPARED
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause_track)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }
}
