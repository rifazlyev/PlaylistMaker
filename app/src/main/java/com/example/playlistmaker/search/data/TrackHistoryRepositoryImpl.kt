package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.common.PreferencesConstants.SEARCH_HISTORY_KEY
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TrackHistoryRepository
import com.google.gson.Gson

class TrackHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : TrackHistoryRepository {

    private var searchHistoryTrackList: MutableList<Track> = mutableListOf()

    override fun loadHistoryTrackList(): MutableList<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, "")
        if (!json.isNullOrEmpty()) {
            searchHistoryTrackList = gson.fromJson(json, Array<Track>::class.java).toMutableList()
        }
        return searchHistoryTrackList
    }

    override fun addTrackToSearchHistoryList(track: Track) {
        searchHistoryTrackList.removeIf {
            it.trackId == track.trackId
        }
        searchHistoryTrackList.add(0, track)

        if (searchHistoryTrackList.size > 10) {
            searchHistoryTrackList.removeAt(searchHistoryTrackList.lastIndex)
        }
        saveSearchHistory()
    }

    override fun clearSearchHistoryTrackList() {
        searchHistoryTrackList.clear()
        saveSearchHistory()
    }

    private fun saveSearchHistory() {
        val json: String = gson.toJson(searchHistoryTrackList)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    override fun getTrackById(id: Int): Track {
        return loadHistoryTrackList().first { it.trackId == id }
    }
}
