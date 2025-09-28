package com.example.playlistmaker.media.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.ui.model.PlaylistUi
import com.example.playlistmaker.media.ui.playlist.detailsPlaylist.PlaylistDetailFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel<PlaylistsViewModel>()

    private val playlistAdapter = PlaylistAdapter(object : OnPlaylistClickListener {
        override fun onPlaylistClick(playlistUi: PlaylistUi) {
            findNavController().navigate(
                R.id.playlistDetailFragment,
                PlaylistDetailFragment.createArg(playlistUi.id)
            )
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_create_playlist_global
            )
        }
        binding.playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistRecyclerView.adapter = playlistAdapter
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(playlistUiState: PlaylistUiState) {
        when (playlistUiState) {
            is PlaylistUiState.Empty -> showEmptyScreen()
            is PlaylistUiState.Content -> showContent(playlistUiState.playlists)
        }
    }

    private fun showEmptyScreen() {
        binding.playlistRecyclerView.visibility = View.GONE
        binding.emptyPlaylistPlaceholder.visibility = View.VISIBLE
    }

    private fun showContent(playlists: List<PlaylistUi>) {
        binding.emptyPlaylistPlaceholder.visibility = View.GONE
        binding.playlistRecyclerView.visibility = View.VISIBLE
        playlistAdapter.listOfPlaylists.clear()
        playlistAdapter.listOfPlaylists.addAll(playlists)
        playlistAdapter.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): Fragment {
            return PlaylistsFragment()
        }
    }
}
