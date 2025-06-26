package com.example.playlistmaker.sharing.domain

data class EmailData(
    val listOfEmail: Array<String> = arrayOf(),
    val subject: String,
    val text: String
)
