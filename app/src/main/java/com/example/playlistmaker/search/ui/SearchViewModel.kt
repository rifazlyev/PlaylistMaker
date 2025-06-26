package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.common.Creator
import com.example.playlistmaker.common.Creator.getHandler
import com.example.playlistmaker.search.domain.TrackInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.mapper.toTrackDomain
import com.example.playlistmaker.search.ui.mapper.toTrackUi
import com.example.playlistmaker.search.ui.model.TrackUi

class SearchViewModel(private val trackInteractor: TrackInteractor) : ViewModel() {
    private var lastSearch: String? = null
    private val handler = getHandler()

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(context: Context): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { SearchViewModel(Creator.provideTrackInteractor(context)) }
            }
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
