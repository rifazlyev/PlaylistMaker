package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.media.ui.model.PlaylistUi
import com.example.playlistmaker.search.ui.model.TrackUi
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistDetailFragment : Fragment() {
    private val playlistDetailsViewModel: PlaylistDetailsViewModel by viewModel<PlaylistDetailsViewModel>()
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButtonPlaylistScreen.setOnClickListener {
            findNavController().navigateUp()
        }

        val bottomSheetContainer = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior.apply {
            isHideable = true
            //делаю с учетом маленьких экранов, закрыть полностью нельзя, но кнопки при этом видно
            peekHeight = resources.getDimensionPixelSize(R.dimen._54dp)
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        val playlistId = requireArguments().getLong(PLAYLIST_ID)
        playlistDetailsViewModel.loadPlaylist(playlistId)
        playlistDetailsViewModel.observePlaylist().observe(viewLifecycleOwner) {
            renderPlaylistImageNameAndDescription(it)
        }
        playlistDetailsViewModel.observeTrackInPlaylist().observe(viewLifecycleOwner) {
            renderPlaylistTracksCountAndDuration(it)
        }
        playlistDetailsViewModel.observeState().observe(viewLifecycleOwner) {
            renderContent(it)
        }
    }

    private fun renderPlaylistImageNameAndDescription(playlistUi: PlaylistUi) {
        Glide.with(this)
            .load(playlistUi.coverPath)
            .placeholder(R.drawable.ic_placeholder_player_screen)
            .centerCrop()
            .into(binding.playlistImage)
        binding.playlistName.text = playlistUi.name
        binding.playlistDescription.isVisible = playlistUi.description.isNotBlank()
        binding.playlistDescription.text = playlistUi.description
    }

    private fun renderPlaylistTracksCountAndDuration(list: List<TrackUi>) {
        val tracksCount = list.size
        val tracksCountText =
            resources.getQuantityString(R.plurals.playlist_tracks_count, tracksCount, tracksCount)
        binding.playlistTracksCount.text = tracksCountText

        val tracksDuration = list.sumOf { it.trackTime }
        val totalMinutes = (tracksDuration / 60000).toInt()
        val tracksDurationText =
            resources.getQuantityString(R.plurals.playlist_duration, totalMinutes, totalMinutes)
        binding.playlistDurationCount.text = tracksDurationText
    }

    private fun renderContent(state: PlaylistDetailsUiState) {

    }

    private fun showTracks() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PLAYLIST_ID = "playlist"
        fun createArg(playlistId: Long) = bundleOf(PLAYLIST_ID to playlistId)
    }
}
