package com.example.playlistmaker.media.domain

import android.net.Uri

interface FileRepository {
    suspend fun copyToPrivateStorage(uri: Uri): String?
}
