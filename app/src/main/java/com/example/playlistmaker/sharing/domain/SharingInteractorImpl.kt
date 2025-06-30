package com.example.playlistmaker.sharing.domain

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())

    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }


    private fun getShareAppLink(): String {
        return "https://practicum.yandex.ru/android-developer"
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            listOfEmail = arrayOf("ruslanfazl@ya.ru"),
            subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker",
            text = "Спасибо разработчикам и разработчицам за крутое приложение!"
        )
    }

    private fun getTermsLink(): String {
        return "https://yandex.ru/legal/practicum_offer/"
    }
}
