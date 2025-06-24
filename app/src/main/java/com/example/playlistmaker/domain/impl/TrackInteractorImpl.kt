package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.common.Resource
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TrackInteractorImpl(
    private val trackRepository: TrackRepository,
    private val trackHistoryRepository: TrackHistoryRepository
) : TrackInteractor {

    override fun searchTrack(expression: String, consumer: TrackInteractor.TrackConsumer) {
        val t = Thread {
            when (val resource = trackRepository.searchTrack(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
        t.start()
    }

    override fun loadHistoryTrackList(): MutableList<Track> {
        return trackHistoryRepository.loadHistoryTrackList()
    }

    override fun addTrackToSearchHistoryList(track: Track) {
        trackHistoryRepository.addTrackToSearchHistoryList(track)
    }

    override fun clearSearchHistoryTrackList() {
        trackHistoryRepository.clearSearchHistoryTrackList()
    }
}
