package com.example.playlistmaker.storage

import android.content.SharedPreferences
import com.example.playlistmaker.constants.PreferencesConstants.SEARCH_HISTORY_KEY
import com.example.playlistmaker.model.Track
import com.google.gson.Gson

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    var searchHistoryTrackList: MutableList<Track> = mutableListOf()

    fun loadHistoryTrackList() {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, "")
        if (json.isNullOrEmpty()) {
            return
        }
        searchHistoryTrackList = gson.fromJson(json, Array<Track>::class.java).toMutableList()
    }

    fun addTrackToSearchHistoryList(track: Track) {
        searchHistoryTrackList.removeIf {
            it.trackId == track.trackId
        }
        searchHistoryTrackList.add(0, track)

        if (searchHistoryTrackList.size > 10) {
            searchHistoryTrackList.removeAt(searchHistoryTrackList.lastIndex)
        }
        saveSearchHistory()
    }

    fun clearSearchHistoryTrackList() {
        searchHistoryTrackList.clear()
        saveSearchHistory()
    }

    private fun saveSearchHistory() {
        val json: String = gson.toJson(searchHistoryTrackList)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }
}
