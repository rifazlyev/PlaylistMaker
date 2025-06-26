package com.example.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.SearchViewModel
import com.example.playlistmaker.TrackUiState
import com.example.playlistmaker.common.Creator.getHandler
import com.example.playlistmaker.common.IntentKeys.TRACK
import com.example.playlistmaker.common.PreferencesConstants.SEARCH_TEXT_KEY
import com.example.playlistmaker.common.UiUtils.hideKeyboard
import com.example.playlistmaker.presentation.OnTrackClickListener
import com.example.playlistmaker.presentation.model.TrackUi
import com.example.playlistmaker.ui.player.PlayerActivity

@SuppressLint("NotifyDataSetChanged")
class SearchActivity : AppCompatActivity() {

    private var isClickAllowed = true
    private val handler = getHandler()
    private lateinit var viewModel: SearchViewModel
    private var textWatcher: TextWatcher? = null

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var backSearch: LinearLayout
    private lateinit var buttonBack: ImageButton
    private lateinit var inputEditText: EditText
    private lateinit var buttonClear: ImageView
    private lateinit var imagePlaceholder: ImageView
    private lateinit var textPlaceholder: TextView
    private lateinit var refreshButton: Button
    private lateinit var clearSearchHistory: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var searchHistoryViewGroup: LinearLayout
    private lateinit var searchResultRecycler: RecyclerView
    private lateinit var searchHistoryRecycler: RecyclerView


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
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backSearch = findViewById(R.id.search_screen)
        buttonBack = findViewById(R.id.back_button_search_screen)
        inputEditText = findViewById(R.id.searchEditText)
        buttonClear = findViewById(R.id.clearButton)
        imagePlaceholder = findViewById(R.id.image_placeholder)
        textPlaceholder = findViewById(R.id.text_placeholder)
        refreshButton = findViewById(R.id.refresh_button)
        searchHistoryViewGroup = findViewById(R.id.search_history_view_group)
        clearSearchHistory = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progress_bar)
        searchResultRecycler = findViewById(R.id.searchResultRecyclerView)
        searchHistoryRecycler = findViewById(R.id.searchHistoryRecyclerView)

        searchResultRecycler.layoutManager = LinearLayoutManager(this)
        searchHistoryRecycler.layoutManager = LinearLayoutManager(this)


        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getFactory(this)
        ).get(SearchViewModel::class.java)

        viewModel.observeState().observe(this) {
            render(it)
        }

        searchResultRecycler.adapter = searchResultTrackAdapter
        searchHistoryRecycler.adapter = searchHistoryTrackAdapter

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                viewModel.loadSearchHistory()
            }
        }
        inputEditText.requestFocus()

        backSearch.setOnClickListener {
            finish()
        }

        buttonBack.setOnClickListener {
            finish()
        }

        buttonClear.setOnClickListener {
            inputEditText.setText("")
            searchResultTrackAdapter.trackList.clear()
            inputEditText.clearFocus()
            this.hideKeyboard(inputEditText)
            buttonClear.visibility = View.GONE
            viewModel.clearLastSearch()
            hideStubViews()
            hideHistory()
        }

        refreshButton.setOnClickListener {
            textPlaceholder.text.toString().let {
                viewModel.searchDebounce(it)
            }
        }

        clearSearchHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                buttonClear.visibility = clearButtonVisibility(p0)
                hideHistory()
                viewModel.searchDebounce(p0.toString())
            }
        }
        inputEditText.addTextChangedListener(textWatcher)
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
        outState.putString(SEARCH_TEXT_KEY, inputEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        inputEditText.setText(restoredText)
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
        progressBar.visibility = View.GONE
        searchResultTrackAdapter.trackList.clear()
        searchResultTrackAdapter.notifyDataSetChanged()

        imagePlaceholder.apply {
            setBackgroundResource(iconResId)
            visibility = View.VISIBLE
        }

        textPlaceholder.apply {
            text = message
            visibility = View.VISIBLE
        }

        refreshButton.visibility = if (showRefreshButton) View.VISIBLE else View.GONE
    }

    private fun showLoad() {
        hideStubViews()
        searchResultRecycler.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun showContent(listOfTrack: List<TrackUi>) {
        hideStubViews()
        progressBar.visibility = View.GONE
        searchResultRecycler.visibility = View.VISIBLE

        searchResultTrackAdapter.trackList.clear()
        searchResultTrackAdapter.trackList.addAll(listOfTrack)
        searchResultTrackAdapter.notifyDataSetChanged()
    }

    private fun hideStubViews() {
        imagePlaceholder.visibility = View.GONE
        textPlaceholder.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    private fun showHistory(tracks: List<TrackUi>) {
        hideStubViews()
        searchResultTrackAdapter.trackList.clear()
        searchResultRecycler.visibility = View.GONE
        searchResultTrackAdapter.notifyDataSetChanged()
        searchHistoryTrackAdapter.trackList.clear()
        searchHistoryTrackAdapter.trackList.addAll(tracks)
        searchHistoryTrackAdapter.notifyDataSetChanged()
        searchHistoryViewGroup.visibility = View.VISIBLE
    }

    private fun hideHistory() {
        searchHistoryViewGroup.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let {
            inputEditText.removeTextChangedListener(it)
        }
    }

    private fun hideAll() {
        hideStubViews()
        progressBar.visibility = View.GONE
        searchResultRecycler.visibility = View.GONE
        hideHistory()
    }
}
