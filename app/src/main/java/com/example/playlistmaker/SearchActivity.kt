package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.adapter.TrackAdapter
import com.example.playlistmaker.constants.IntentKeys.TRACK
import com.example.playlistmaker.constants.PreferencesConstants.PLAYLIST_PREFERENCES
import com.example.playlistmaker.constants.PreferencesConstants.SEARCH_TEXT_KEY
import com.example.playlistmaker.listeners.OnTrackClickListener
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.requests.ITunesApiService
import com.example.playlistmaker.responses.SearchResponse
import com.example.playlistmaker.storage.SearchHistory
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("NotifyDataSetChanged")
class SearchActivity : AppCompatActivity() {
    private var textValue: String = ""
    private val baseUrl = "https://itunes.apple.com"
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private lateinit var backSearch: LinearLayout
    private lateinit var buttonBack: ImageButton
    private lateinit var inputEditText: EditText
    private lateinit var buttonClear: ImageView
    private lateinit var imagePlaceholder: ImageView
    private lateinit var textPlaceholder: TextView
    private lateinit var refreshButton: Button
    private lateinit var clearSearchHistory: Button
    private lateinit var searchHistoryViewGroup: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchResultTrackAdapter: TrackAdapter
    private lateinit var searchHistoryTrackAdapter: TrackAdapter
    private lateinit var searchResultRecycler: RecyclerView
    private lateinit var searchHistoryRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar

    private val searchRunnable = Runnable { search() }

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApiService::class.java)
    private val listOfTrack: MutableList<Track> = mutableListOf()

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
        sharedPreferences = getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)
        searchHistory.loadHistoryTrackList()

        searchHistoryTrackAdapter = TrackAdapter(object : OnTrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                    playerIntent.putExtra(TRACK, track)
                    startActivity(playerIntent)
                }
            }
        }
        )

        searchResultTrackAdapter = TrackAdapter(object : OnTrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    searchHistory.addTrackToSearchHistoryList(track)
                    searchHistoryTrackAdapter.notifyDataSetChanged()
                    val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                    playerIntent.putExtra(TRACK, track)
                    startActivity(playerIntent)
                }
            }
        }
        )

        searchResultTrackAdapter.trackList = listOfTrack
        searchHistoryTrackAdapter.trackList = searchHistory.searchHistoryTrackList

        searchResultRecycler = findViewById(R.id.searchResultRecyclerView)
        searchHistoryRecycler = findViewById(R.id.searchHistoryRecyclerView)

        searchResultRecycler.layoutManager = LinearLayoutManager(this)
        searchHistoryRecycler.layoutManager = LinearLayoutManager(this)

        searchResultRecycler.adapter = searchResultTrackAdapter
        searchHistoryRecycler.adapter = searchHistoryTrackAdapter

        //Хоть и добавил автопоиск через handler, эту функцию решил оставить для ручного поиска
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = inputEditText.text.toString()
                if (query.isNotEmpty()) {
                    search()
                }
                true
            } else {
                false
            }
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            searchHistoryIsVisible(hasFocus && inputEditText.text.isEmpty())
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
            listOfTrack.clear()
            inputEditText.clearFocus()
            hideKeyboard(inputEditText)
            buttonClear.visibility = View.GONE
            allViewIsGone()
            searchHistoryIsVisible(false)
        }

        refreshButton.setOnClickListener {
            search()
        }

        clearSearchHistory.setOnClickListener {
            searchHistory.clearSearchHistoryTrackList()
            searchHistoryIsVisible(false)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrBlank()) {
                    textValue = p0.toString()
                    buttonClear.visibility = clearButtonVisibility(p0)
                    searchHistoryIsVisible(false)
                    searchDebounce()
                } else {
                    searchHistoryIsVisible(true)
                    searchResultRecycler.visibility = View.GONE
                    allViewIsGone()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
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

    private fun hideKeyboard(view: View) {
        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, textValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        inputEditText.setText(restoredText)
    }

    private fun showError(
        message: String,
        iconResId: Int,
        showRefreshButton: Boolean = false
    ) {
        listOfTrack.clear()
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

    private fun allViewIsGone() {
        imagePlaceholder.visibility = View.GONE
        textPlaceholder.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    private fun search() {
        val query = inputEditText.text.toString()
        if (query.isBlank()) return
        allViewIsGone()
        progressBar.visibility = View.VISIBLE
        searchResultRecycler.visibility = View.GONE
        iTunesService.search(query).enqueue(
            object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    listOfTrack.clear()
                    searchResultTrackAdapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        allViewIsGone()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            listOfTrack.addAll(response.body()?.results!!)
                            searchResultTrackAdapter.notifyDataSetChanged()
                            searchResultRecycler.visibility = View.VISIBLE
                            Log.d("query", Gson().toJson(response.body()))
                        } else {
                            showError(getString(R.string.empty_list), R.drawable.ic_empty_list)
                        }
                    } else {
                        showError(
                            getString(R.string.something_wrong),
                            R.drawable.ic_something_wrong,
                            true
                        )
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    showError(
                        getString(R.string.something_wrong),
                        R.drawable.ic_something_wrong,
                        true
                    )
                }
            }
        )
    }

    fun searchHistoryIsVisible(flag: Boolean) {
        if (searchHistory.searchHistoryTrackList.isEmpty()) {
            searchHistoryViewGroup.visibility = View.GONE
        } else {
            searchHistoryViewGroup.visibility = if (flag) View.VISIBLE else View.GONE
            searchHistoryTrackAdapter.trackList = searchHistory.searchHistoryTrackList
            searchHistoryTrackAdapter.notifyDataSetChanged()
        }
    }

    private fun clickDebounce(): Boolean {
        val currentValue = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return currentValue
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
}
