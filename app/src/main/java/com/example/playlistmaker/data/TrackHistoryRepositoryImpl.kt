package com.example.playlistmaker.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.common.PreferencesConstants.PLAYLIST_PREFERENCES
import com.example.playlistmaker.common.PreferencesConstants.SEARCH_HISTORY_KEY
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class TrackHistoryRepositoryImpl(
    context: Context
) : TrackHistoryRepository {
    private val gson = Gson()
    private val sharedPreferences = context.getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
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
