package com.example.playlistmaker.requests

import com.example.playlistmaker.responses.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<SearchResponse>
}
