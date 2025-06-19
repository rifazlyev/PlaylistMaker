package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun searchTrack(expression: String, consumer: (Result<List<Track>>) -> Unit) {
        val t = Thread {
            repository.searchTrack(expression, consumer)
        }
        t.start()
    }
}
