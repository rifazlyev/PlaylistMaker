package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.ui.mapper.toTrackDomain
import com.example.playlistmaker.search.ui.mapper.toTrackUi
import com.example.playlistmaker.search.ui.model.TrackUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
) : ViewModel() {
    private var isClickAllowed = true
    private var lastSearch: String? = null
    var lastSearchQuery: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private var searchTrack: Job? = null

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    fun clickDebounce(): Boolean {
        val currentValue = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
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
        searchTrack?.cancel()
        searchTrack = null
    }

    fun searchDebounce(changedText: String) {
        if (changedText.isBlank()) {
            clearLastSearch()
            loadSearchHistory()
            return
        }
        if (lastSearch == changedText) return
        lastSearch = changedText

        searchTrack?.cancel()
        searchTrack = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(lastSearch!!)
        }
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
}
