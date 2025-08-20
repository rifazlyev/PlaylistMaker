package com.example.playlistmaker.common

import java.text.SimpleDateFormat
import java.util.Locale

fun formatTrackTime(durationInMillis: Long): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(durationInMillis)
}
