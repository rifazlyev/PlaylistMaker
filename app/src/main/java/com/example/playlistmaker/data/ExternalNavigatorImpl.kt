package com.example.playlistmaker.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.ExternalNavigator
import com.example.playlistmaker.domain.models.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(link: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        val chooser = Intent.createChooser(intent, context.getString(R.string.choose_the_app))
        context.startActivity(chooser)
    }

    override fun openLink(link: String) {
        val url = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, url)
        context.startActivity(intent)
    }

    override fun openEmail(emailData: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, emailData.listOfEmail)
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.text)
        }
        context.startActivity(intent)
    }
}
