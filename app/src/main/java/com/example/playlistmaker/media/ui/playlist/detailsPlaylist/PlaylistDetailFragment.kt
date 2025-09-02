package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.media.ui.model.PlaylistUi
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.ui.OnTrackClickListener
import com.example.playlistmaker.search.ui.model.TrackUi
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistDetailFragment : Fragment() {
    private val playlistDetailsViewModel: PlaylistDetailsViewModel by viewModel<PlaylistDetailsViewModel>()
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private val tracksAdapter = TrackInPlaylistAdapter(
        object : OnTrackClickListener {
            override fun onTrackClick(track: TrackUi) {
                openPlayer(track)
            }
        },
        object : OnTrackInPlaylistLongClickListener {
            override fun onTrackLongClickListener(trackUi: TrackUi): Boolean {
                showConfirmDialog()
                return true
            }
        }
    )

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
            isHideable = false
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
        binding.playlistDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistDetailsRecyclerView.adapter = tracksAdapter
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Хотите удалить трек")
            .setNegativeButton("Нет",null)
            .setPositiveButton("Да", null)
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
        when (state) {
            is PlaylistDetailsUiState.Empty -> showEmpty()
            is PlaylistDetailsUiState.Content -> showTracks(state.tracks)
        }

    }

    private fun showTracks(list: List<TrackUi>) {
        binding.emptyPlaylistPlaceholder.isVisible = false
        binding.playlistDetailsRecyclerView.isVisible = true
        tracksAdapter.listOfTracks.clear()
        tracksAdapter.listOfTracks.addAll(list)
        tracksAdapter.notifyDataSetChanged()
    }

    private fun showEmpty() {
        binding.playlistDetailsRecyclerView.isVisible = false
        binding.emptyPlaylistPlaceholder.isVisible = true
    }

    private fun openPlayer(trackUi: TrackUi) {
        findNavController().navigate(
            R.id.action_global_player,
            PlayerFragment.createArg(trackUi)
        )
    }

    private fun showConfirmDialog() {
        val dialog = confirmDialog.create()
        dialog.show()
        //хочу сделать согласованно системе, поэтому крашу кнопки в синий
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
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
