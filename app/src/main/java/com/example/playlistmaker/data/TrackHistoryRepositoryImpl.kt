package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.common.PreferencesConstants.SEARCH_HISTORY_KEY
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class TrackHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences
): TrackHistoryRepository  {
    private val gson = Gson()
    private var searchHistoryTrackList: MutableList<Track> = mutableListOf()


    override fun loadHistoryTrackList(): MutableList<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, "")
        if (!json.isNullOrEmpty()){
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
}