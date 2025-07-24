package com.example.playlistmaker.search.domain

import com.example.playlistmaker.common.Resource
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTrack(expression: String): Flow<Resource<List<Track>>>
}
