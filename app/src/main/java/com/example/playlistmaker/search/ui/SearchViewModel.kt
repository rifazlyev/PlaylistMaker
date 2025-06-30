package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.ui.mapper.toTrackDomain
import com.example.playlistmaker.search.ui.mapper.toTrackUi
import com.example.playlistmaker.search.ui.model.TrackUi

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val handler: Handler
) : ViewModel() {
    private var isClickAllowed = true
    private var lastSearch: String? = null

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    fun clickDebounce(): Boolean {
        val currentValue = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return currentValue
    }

    private val stateLiveData = MutableLiveData<TrackUiState>()
    fun observeState(): LiveData<TrackUiState> = stateLiveData

    fun loadSearchHistory() {
        val history = trackInteractor.loadHistoryTrackList()
        renderState(TrackUiState.EmptyScreen)
        if (history.isNotEmpty()) {
            renderState(TrackUiState.HistoryContent(history.map { it.toTrackUi() }))
        }
    }

    fun addTrackToHistory(track: TrackUi) {
        trackInteractor.addTrackToSearchHistoryList(track.toTrackDomain())
    }

    fun clearSearchHistory() {
        trackInteractor.clearSearchHistoryTrackList()
        renderState(TrackUiState.EmptyScreen)
    }

    fun clearLastSearch() {
        lastSearch = null
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (changedText.isBlank()) {
            clearLastSearch()
            loadSearchHistory()
            return
        }

        if (lastSearch == changedText) return

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        lastSearch = changedText

        val searchRunnable = Runnable {
            if (lastSearch.isNullOrBlank()) return@Runnable
            searchRequest(lastSearch!!)
        }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime
        )
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isBlank()) {
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
            return
        }
        renderState(TrackUiState.Loading)

        trackInteractor.searchTrack(newSearchText, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTrack: List<Track>?, errorMessage: String?) {
                handler.post {
                    val listOfTrack = mutableListOf<TrackUi>()
                    if (foundTrack != null) {
                        listOfTrack.addAll(foundTrack.map { it.toTrackUi() })
                    }
                    when {
                        errorMessage != null -> {
                            renderState(TrackUiState.Error)
                        }

                        listOfTrack.isEmpty() -> {
                            renderState(TrackUiState.EmptyResult)

                        }

                        else -> {
                            renderState(TrackUiState.Content(listOfTrack))
                        }

                    }
                }
            }
        })
    }

    private fun renderState(state: TrackUiState) {
        stateLiveData.postValue(state)

    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}
