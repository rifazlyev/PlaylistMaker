package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.common.PreferencesConstants.SEARCH_HISTORY_KEY
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TrackHistoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class TrackHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase
) : TrackHistoryRepository {

    private var searchHistoryTrackList: MutableList<Track> = mutableListOf()

    override fun loadHistoryTrackList(): MutableList<Track> {
        val favoriteTracksId = runBlocking(Dispatchers.IO) {
            appDatabase.getTrackDao().getFavoriteTracksId()
        }.toSet()
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, "")
        if (!json.isNullOrEmpty()) {
            searchHistoryTrackList =
                gson.fromJson(json, Array<Track>::class.java).map { track: Track ->
                    track.copy(isFavorite = favoriteTracksId.contains(track.trackId))
                }.toMutableList()
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
