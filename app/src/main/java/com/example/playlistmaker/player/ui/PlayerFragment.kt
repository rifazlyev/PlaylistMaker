package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.UiUtils.dpToPx
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.search.ui.mapper.toTrackUi
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

        val trackId = requireArguments().getInt(TRACK_ID, -1)
        if (trackId == -1) {
            Toast.makeText(
                requireContext(),
                getString(R.string.track_not_found),
                Toast.LENGTH_SHORT
            ).show()
            requireActivity().supportFragmentManager.popBackStack()
            return
        }

        binding.backButtonPlayerScreen.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.playerAlbumInfoGroup.visibility = View.GONE

        playerViewModel.initializePlayer(trackId)

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

        playerViewModel.observeTrackLiveData().observe(viewLifecycleOwner) { trackDomain ->
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

        Glide.with(this)
            .load(trackUi.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_player_screen)
            .centerCrop()
            .transform(RoundedCorners(radiusPx))
            .into(binding.playerImagePlaceholder)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TRACK_ID = "trackId"

        fun newInstance(trackId: Int) = PlayerFragment().apply {
            arguments = bundleOf(TRACK_ID to trackId)
        }
    }
}
