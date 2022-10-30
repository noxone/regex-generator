package org.olafneumann.regex.generator.ui.components

import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement

object CookieBanner {
    private const val ID_DIV_COOKIE = "rg_container_cookie"
    private const val ID_BTN_ACCEPT = "rg_btn_cookie_accept"
    private const val ID_BTN_REJECT = "rg_btn_cookie_reject"

    private val ctnCookie: HTMLDivElement = HtmlHelper.getElementById(ID_DIV_COOKIE)

    fun initialize(hasUserConsent: () -> Boolean, setUserConsent: (Boolean) -> Unit) {
        if (hasUserConsent()) {
            hideBanner()
        } else {
            val btnAccept: HTMLButtonElement = HtmlHelper.getElementById(ID_BTN_ACCEPT)
            val btnReject: HTMLButtonElement = HtmlHelper.getElementById(ID_BTN_REJECT)

            btnAccept.onclick = {
                setUserConsent(true)
                hideBanner()
            }
            btnReject.onclick = { hideBanner() }
        }
    }

    private fun hideBanner() {
        jQuery(ctnCookie).hide()
    }
}
