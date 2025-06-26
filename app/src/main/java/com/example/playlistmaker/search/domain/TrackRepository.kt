package com.example.playlistmaker.search.domain

import com.example.playlistmaker.common.Resource

interface TrackRepository {
    fun searchTrack(expression: String): Resource<List<Track>>
}
