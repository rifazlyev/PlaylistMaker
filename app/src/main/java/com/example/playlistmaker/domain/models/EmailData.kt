package com.example.playlistmaker.domain.models

data class EmailData(
    val listOfEmail: Array<String> = arrayOf(),
    val subject: String,
    val text: String
)
