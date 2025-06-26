package com.example.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.SearchViewModel
import com.example.playlistmaker.TrackUiState
import com.example.playlistmaker.common.Creator.getHandler
import com.example.playlistmaker.common.IntentKeys.TRACK
import com.example.playlistmaker.common.PreferencesConstants.SEARCH_TEXT_KEY
import com.example.playlistmaker.common.UiUtils.hideKeyboard
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.presentation.OnTrackClickListener
import com.example.playlistmaker.presentation.model.TrackUi
import com.example.playlistmaker.ui.player.PlayerActivity

@SuppressLint("NotifyDataSetChanged")
class SearchActivity : AppCompatActivity() {

    private var isClickAllowed = true
    private val handler = getHandler()
    private lateinit var viewModel: SearchViewModel
    private var textWatcher: TextWatcher? = null
    private lateinit var binding: ActivitySearchBinding

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val searchHistoryTrackAdapter = TrackAdapter(object : OnTrackClickListener {
        override fun onTrackClick(track: TrackUi) {
            if (clickDebounce()) {
                openPlayer(track)
            }
        }
    }
    )

    private val searchResultTrackAdapter = TrackAdapter(object : OnTrackClickListener {
        override fun onTrackClick(track: TrackUi) {
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                openPlayer(track)
            }
        }
    }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.searchResultRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)


        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getFactory(this)
        ).get(SearchViewModel::class.java)

        viewModel.observeState().observe(this) {
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


        binding.searchScreen.setOnClickListener {
            finish()
        }

        binding.backButtonSearchScreen.setOnClickListener {
            finish()
        }

        binding.clearButton.setOnClickListener {
            binding.searchEditText.setText("")
            searchResultTrackAdapter.trackList.clear()
            binding.searchScreen.clearFocus()
            this.hideKeyboard(binding.searchScreen)
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        binding.searchEditText.setText(restoredText)
    }

    private fun clickDebounce(): Boolean {
        val currentValue = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return currentValue
    }

    private fun openPlayer(track: TrackUi) {
        val playerIntent = Intent(this, PlayerActivity::class.java)
        playerIntent.putExtra(TRACK, track.trackId)
        startActivity(playerIntent)
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

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let {
            binding.searchEditText.removeTextChangedListener(it)
        }
    }

    private fun hideAll() {
        hideStubViews()
        binding.progressBar.visibility = View.GONE
        binding.searchResultRecyclerView.visibility = View.GONE
        hideHistory()
    }
}
