package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.UiUtils.dpToPx
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.search.ui.model.TrackUi
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {
    private val playerViewModel: PlayerViewModel by viewModel<PlayerViewModel>()
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trackUi: TrackUi? = requireArguments().getParcelable(TRACK)
        if (trackUi == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.track_not_found),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
            return
        }

        playerViewModel.initializePlayer(trackUi)

        binding.backButtonPlayerScreen.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.playerAlbumInfoGroup.visibility = View.GONE

        playerViewModel.observePlayerState().observe(viewLifecycleOwner) {
            if (it == PlayerViewModel.PlayerState.Playing) {
                enabledPauseButton()
            } else {
                enablePlayButton()
            }
        }

        playerViewModel.observeProgressTime().observe(viewLifecycleOwner) {
            binding.playerTrackDuration.text = it
        }

        binding.playerPlayButton.setOnClickListener {
            playerViewModel.onPlayButtonClicked()
        }

        playerViewModel.observeTrackLiveData().observe(viewLifecycleOwner) {
            renderTrackUi(it)
        }

        binding.playerLikeButton.setOnClickListener {
            if (playerViewModel.favoriteClickDebounce())
                playerViewModel.onFavoriteClicked()
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
        val radiusPx = dpToPx(8F, context = requireContext())

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
        binding.playerLikeButton.isActivated = trackUi.isFavorite
        renderFavoriteIcon(trackUi.isFavorite)

        Glide.with(this)
            .load(trackUi.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_player_screen)
            .centerCrop()
            .transform(RoundedCorners(radiusPx))
            .into(binding.playerImagePlaceholder)
    }

    private fun renderFavoriteIcon(flag: Boolean) {
        when (flag) {
            true -> binding.playerLikeButton.setImageResource(R.drawable.ic_heart_fill)
            else -> binding.playerLikeButton.setImageResource(R.drawable.ic_heart)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TRACK = "track"
        fun createArg(track: TrackUi) = bundleOf(TRACK to track)
    }
}
