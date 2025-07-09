package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.common.PreferencesConstants.SEARCH_TEXT_KEY
import com.example.playlistmaker.common.UiUtils.hideKeyboard
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.ui.model.TrackUi
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("NotifyDataSetChanged")
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModel<SearchViewModel>()
    private var textWatcher: TextWatcher? = null
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchHistoryTrackAdapter = TrackAdapter(object : OnTrackClickListener {
        override fun onTrackClick(track: TrackUi) {
            if (viewModel.clickDebounce()) {
                openPlayer(track)
            }
        }
    }
    )

    private val searchResultTrackAdapter = TrackAdapter(object : OnTrackClickListener {
        override fun onTrackClick(track: TrackUi) {
            if (viewModel.clickDebounce()) {
                viewModel.addTrackToHistory(track)
                openPlayer(track)
            }
        }
    }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val restoredText = savedInstanceState?.getString(SEARCH_TEXT_KEY, "")
        binding.searchEditText.setText(restoredText)

        binding.searchResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.searchResultRecyclerView.adapter = searchResultTrackAdapter
        binding.searchHistoryRecyclerView.adapter = searchHistoryTrackAdapter

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) {
                viewModel.loadSearchHistory()
            }
        }

        binding.searchEditText.requestFocus()

        binding.clearButton.setOnClickListener {
            binding.searchEditText.setText("")
            searchResultTrackAdapter.trackList.clear()
            binding.searchEditText.clearFocus()
            requireActivity().hideKeyboard(binding.searchEditText)
            binding.clearButton.visibility = View.GONE
            viewModel.clearLastSearch()
            hideStubViews()
            hideHistory()
        }

        binding.refreshButton.setOnClickListener {
            binding.textPlaceholder.text.toString().let {
                viewModel.searchDebounce(it)
            }
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                binding.clearButton.visibility = clearButtonVisibility(p0)
                hideHistory()
                viewModel.searchDebounce(p0.toString())
            }
        }
        binding.searchEditText.addTextChangedListener(textWatcher)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, binding.searchEditText.text.toString())
    }

    private fun render(state: TrackUiState) {
        when (state) {
            is TrackUiState.EmptyResult -> showError(
                getString(R.string.empty_list),
                R.drawable.ic_empty_list
            )

            is TrackUiState.Error -> showError(
                getString(R.string.something_wrong),
                R.drawable.ic_something_wrong,
                true
            )

            is TrackUiState.Loading -> showLoad()

            is TrackUiState.Content -> showContent(state.tracks)

            is TrackUiState.HistoryContent -> showHistory(state.tracks)
            is TrackUiState.EmptyScreen -> hideAll()
        }
    }

    private fun showError(
        message: String,
        iconResId: Int,
        showRefreshButton: Boolean = false
    ) {
        binding.progressBar.visibility = View.GONE
        searchResultTrackAdapter.trackList.clear()
        searchResultTrackAdapter.notifyDataSetChanged()

        binding.imagePlaceholder.apply {
            setBackgroundResource(iconResId)
            visibility = View.VISIBLE
        }

        binding.textPlaceholder.apply {
            text = message
            visibility = View.VISIBLE
        }

        binding.refreshButton.visibility = if (showRefreshButton) View.VISIBLE else View.GONE
    }

    private fun showLoad() {
        hideStubViews()
        binding.searchResultRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showContent(listOfTrack: List<TrackUi>) {
        hideStubViews()
        binding.progressBar.visibility = View.GONE
        binding.searchResultRecyclerView.visibility = View.VISIBLE

        searchResultTrackAdapter.trackList.clear()
        searchResultTrackAdapter.trackList.addAll(listOfTrack)
        searchResultTrackAdapter.notifyDataSetChanged()
    }

    private fun hideStubViews() {
        binding.imagePlaceholder.visibility = View.GONE
        binding.textPlaceholder.visibility = View.GONE
        binding.refreshButton.visibility = View.GONE
    }

    private fun showHistory(tracks: List<TrackUi>) {
        hideStubViews()
        searchResultTrackAdapter.trackList.clear()
        binding.searchResultRecyclerView.visibility = View.GONE
        searchResultTrackAdapter.notifyDataSetChanged()
        searchHistoryTrackAdapter.trackList.clear()
        searchHistoryTrackAdapter.trackList.addAll(tracks)
        searchHistoryTrackAdapter.notifyDataSetChanged()
        binding.searchHistoryViewGroup.visibility = View.VISIBLE
    }

    private fun hideHistory() {
        binding.searchHistoryViewGroup.visibility = View.GONE
    }

    private fun hideAll() {
        hideStubViews()
        binding.progressBar.visibility = View.GONE
        binding.searchResultRecyclerView.visibility = View.GONE
        hideHistory()
    }

    private fun openPlayer(track: TrackUi) {
        findNavController().navigate(
            R.id.action_searchFragment_to_playerFragment,
            PlayerFragment.createArg(track.trackId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let {
            binding.searchEditText.removeTextChangedListener(it)
        }
        _binding = null
    }
}
