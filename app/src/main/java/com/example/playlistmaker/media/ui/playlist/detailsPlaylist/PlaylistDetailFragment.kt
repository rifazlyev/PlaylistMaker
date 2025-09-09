package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.media.ui.mapper.toPlaylistUi
import com.example.playlistmaker.media.ui.model.PlaylistUi
import com.example.playlistmaker.media.ui.playlist.createPlaylist.CreateEditPlaylistFragment
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.ui.OnTrackClickListener
import com.example.playlistmaker.search.ui.mapper.toTrackUi
import com.example.playlistmaker.search.ui.model.TrackUi
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailFragment : Fragment() {
    private val playlistDetailsViewModel: PlaylistDetailsViewModel by viewModel<PlaylistDetailsViewModel>()
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var confirmDeleteTrackDialog: MaterialAlertDialogBuilder
    private var trackId: Long? = null
    private var playlistId: Long? = null
    private val tracksAdapter = TrackInPlaylistAdapter(
        object : OnTrackClickListener {
            override fun onTrackClick(track: TrackUi) {
                openPlayer(track)
            }
        },
        object : OnTrackInPlaylistLongClickListener {
            override fun onTrackLongClickListener(trackUi: TrackUi): Boolean {
                trackId = trackUi.trackId
                showConfirmDeleteTrackDialog()
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

        binding.menuButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        binding.deleteButton.setOnClickListener {
            showConfirmDeletePlaylistDialog()
        }

        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet)
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        menuBottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.isVisible = false
                        }

                        BottomSheetBehavior.STATE_EXPANDED -> bottomSheet.post {
                            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }

                        else -> binding.overlay.isVisible = true

                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })

        val args = arguments
        playlistId = if (args?.containsKey(PLAYLIST_ID) == true) args.getLong(PLAYLIST_ID) else null
        playlistDetailsViewModel.loadPlaylist(playlistId)
        playlistDetailsViewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            renderPlaylistImageNameAndDescription(playlist.toPlaylistUi())
        }
        playlistDetailsViewModel.observeTrackInPlaylist().observe(viewLifecycleOwner) { tracks ->
            renderPlaylistTracksCountAndDuration(tracks.map { it.toTrackUi() })
        }
        playlistDetailsViewModel.observeState().observe(viewLifecycleOwner) {
            renderContent(it)
        }

        playlistDetailsViewModel.observeDeletePlaylist().observe(viewLifecycleOwner) {
            deletePlaylistResult(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                playlistDetailsViewModel.observeUiSharingEvent().collect { event ->
                    showNoTracksToast(event)
                }
            }
        }

        binding.sharingButton.setOnClickListener {
            playlistDetailsViewModel.sharePlaylist()
        }

        binding.sharingAction.setOnClickListener {
            playlistDetailsViewModel.sharePlaylist()
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.editButton.setOnClickListener {
            openEditPlaylistScreen(playlistId)
        }

        binding.playlistDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistDetailsRecyclerView.adapter = tracksAdapter

        confirmDeleteTrackDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirm_delete_track))
            .setNegativeButton(getString(R.string.no), null)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                playlistDetailsViewModel.deleteTrack(
                    trackId ?: return@setPositiveButton
                )
            }
    }

    private fun showNoTracksToast(playlistDetailsEvent: PlaylistDetailsEvent) {
        if (playlistDetailsEvent is PlaylistDetailsEvent.ShowNoTracksToast) {
            Toast.makeText(requireContext(), getString(R.string.no_tracks), Toast.LENGTH_SHORT)
                .show()
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
        binding.playlistNameSmall.text = playlistUi.name
        Glide.with(this)
            .load(playlistUi.coverPath)
            .placeholder(R.drawable.ic_placeholder_player_screen)
            .centerCrop()
            .into(binding.playlistImageSmall)
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
        binding.playlistTracksCountSmall.text = tracksCountText
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

    private fun showConfirmDeleteTrackDialog() {
        val dialog = confirmDeleteTrackDialog.create()
        dialog.show()
        //хочу сделать согласованно системе, поэтому крашу кнопки в синий
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
    }

    private fun showConfirmDeletePlaylistDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirm_delete_playlist) + " \"${binding.playlistName.text}\"?")
            .setNegativeButton(getString(R.string.no), null)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                playlistDetailsViewModel.deletePlaylist()
            }
            .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
    }

    private fun deletePlaylistResult(resultCode: Int) {
        if (resultCode > 0) {
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_deleted),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
            return
        }
        Toast.makeText(requireContext(), getString(R.string.deleting_error), Toast.LENGTH_SHORT)
            .show()
    }

    private fun openEditPlaylistScreen(playlistId: Long?) {
        if (playlistId == null) return
        findNavController().navigate(
            R.id.createPlaylistFragment,
            CreateEditPlaylistFragment.createArg(playlistId)
        )
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
