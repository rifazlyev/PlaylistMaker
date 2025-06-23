package com.example.playlistmaker.common

sealed class Resource<T>(val data: T?, val message: String? = null) {
    class Success<T>(data: T?): Resource<T>(data = data)
    class Error<T>(message: String, data: T? = null): Resource<T>(message = message, data = data)
}
