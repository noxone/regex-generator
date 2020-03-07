package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.js.jQuery

object CookieBanner {
    private const val ID_DIV_COOKIE = "rg_container_cookie"
    private const val ID_BTN_ACCEPT = "rg_btn_cookie_accept"
    private const val ID_BTN_REJECT = "rg_btn_cookie_reject"

    private val ctnCookie = HtmlHelper.getDivById(ID_DIV_COOKIE)

    fun initialize() {
        if (ApplicationSettings.hasUserConsent) {
            hideBanner()
        } else {
            val btnAccept = HtmlHelper.getButtonById(ID_BTN_ACCEPT)
            val btnReject = HtmlHelper.getButtonById(ID_BTN_REJECT)

            btnAccept.onclick = {
                ApplicationSettings.hasUserConsent = true
                hideBanner()
            }
            btnReject.onclick = { hideBanner() }
        }
    }

    private fun hideBanner() {
        jQuery(ctnCookie).hide()
    }
}