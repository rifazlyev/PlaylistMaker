package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(emailData: EmailData)
}
