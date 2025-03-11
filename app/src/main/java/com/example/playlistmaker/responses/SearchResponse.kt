package com.example.playlistmaker.responses

import com.example.playlistmaker.model.Track

data class SearchResponse(
    val results: List<Track>
)
