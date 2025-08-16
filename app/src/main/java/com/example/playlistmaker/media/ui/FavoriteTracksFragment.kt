package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.ui.OnTrackClickListener
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.model.TrackUi
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {
    private val viewModel: FavoriteTracksViewModel by viewModel<FavoriteTracksViewModel>()
    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private val favoriteTracksAdapter = TrackAdapter(object : OnTrackClickListener {
        override fun onTrackClick(track: TrackUi) {
            if (viewModel.favoriteClickDebounce())
                openPlayer(track)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favoriteTracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoriteTracksRecyclerView.adapter = favoriteTracksAdapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: FavoriteUiState) {
        when (state) {
            is FavoriteUiState.Empty -> showEmptyScreen()
            is FavoriteUiState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmptyScreen() {
        binding.favoriteTracksRecyclerView.visibility = View.GONE
        binding.emptyFavoriteTracksBlock.visibility = View.VISIBLE
    }

    private fun showContent(listOfTrack: List<TrackUi>) {
        binding.emptyFavoriteTracksBlock.visibility = View.GONE
        binding.favoriteTracksRecyclerView.visibility = View.VISIBLE
        favoriteTracksAdapter.trackList.clear()
        favoriteTracksAdapter.trackList.addAll(listOfTrack)
        favoriteTracksAdapter.notifyDataSetChanged()
    }

    private fun openPlayer(trackUi: TrackUi) {
        findNavController().navigate(
            R.id.action_global_player,
            PlayerFragment.createArg(trackUi)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): Fragment {
            return FavoriteTracksFragment()
        }
    }
}
