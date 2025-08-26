package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.UiUtils.dpToPx
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.media.ui.model.PlaylistUi
import com.example.playlistmaker.media.ui.playlist.OnPlaylistClickListener
import com.example.playlistmaker.search.ui.model.TrackUi
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {
    private val playerViewModel: PlayerViewModel by viewModel<PlayerViewModel>()
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var playlistAdapter: PlaylistPlayerAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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

        playlistAdapter = PlaylistPlayerAdapter(object : OnPlaylistClickListener {
            override fun onPlaylistClick(playlistUi: PlaylistUi) {
                playerViewModel.addTrackToPlaylist(trackUi, playlistUi)
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                playerViewModel.observeAddTrackResult()
                    .collect { addTrackResult ->
                        showResult(addTrackResult)
                    }
            }
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

        playerViewModel.observePlaylistData().observe(viewLifecycleOwner) {
            render(it)
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

        val bottomSheetContainer = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.playerAddTrackButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(
            object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                        }
                        else -> {
                            binding.overlay.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_create_playlist_global)
        }

        binding.playlistRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistRecyclerView.adapter = playlistAdapter

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                findNavController().navigateUp()
            }
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
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

    private fun render(playerPlaylistState: PlayerPlaylistState) {
        when (playerPlaylistState) {
            is PlayerPlaylistState.Empty -> showEmpty()
            is PlayerPlaylistState.Content -> showPlaylists(playerPlaylistState.playlists)
        }
    }

    private fun showPlaylists(playlists: List<PlaylistUi>) {
        binding.playlistRecyclerView.visibility = View.VISIBLE
        playlistAdapter.listOfPlaylists.clear()
        playlistAdapter.listOfPlaylists.addAll(playlists)
        playlistAdapter.notifyDataSetChanged()
    }

    private fun showEmpty() {
        binding.playlistRecyclerView.visibility = View.GONE
    }

    private fun showResult(addTrackResult: AddTrackResult) {
        when (addTrackResult) {
            is AddTrackResult.Success -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                showToast("Добавлено в плейлист ${addTrackResult.playlist.name}")
            }

            is AddTrackResult.AlreadyExist -> showToast("Трек уже добавлен в плейлист ${addTrackResult.playlist.name}")
            is AddTrackResult.Error -> showToast("Ошибка добавления, попробуйте еще раз")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
