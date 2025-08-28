package com.example.playlistmaker.media.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.playlistmaker.media.domain.FileRepository
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class FileRepositoryImpl(private val context: Context) : FileRepository {
    override suspend fun copyToPrivateStorage(uri: Uri): String? {
        return try {
            val coversDir = File(context.filesDir, "playlist_cover")
            if (!coversDir.exists()) coversDir.mkdirs()

            val mimeType = context.contentResolver.getType(uri)
            val extension = when (mimeType) {
                "image/png" -> ".png"
                "image/webp" -> ".webp"
                else -> ".jpg"
            }

            // Генерируем уникальное имя файла
            val fileName = "${UUID.randomUUID()}$extension"
            val destFile = File(coversDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            return destFile.absolutePath
        } catch (e: Exception) {
            Log.d("FILE", "Ошибка копирования")
            null
        }
    }
}
