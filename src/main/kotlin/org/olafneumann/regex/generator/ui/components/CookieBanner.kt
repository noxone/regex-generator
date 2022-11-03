package org.olafneumann.regex.generator.ui.components

import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement

class CookieBanner(
    private val onDone: (Boolean) -> Unit
) {
    companion object {
        private const val ID_DIV_COOKIE = "rg_container_cookie"
        private const val ID_BTN_ACCEPT = "rg_btn_cookie_accept"
        private const val ID_BTN_REJECT = "rg_btn_cookie_reject"
    }

    private val ctnCookie: HTMLDivElement = HtmlHelper.getElementById(ID_DIV_COOKIE)

    init {
        val btnAccept: HTMLButtonElement = HtmlHelper.getElementById(ID_BTN_ACCEPT)
        val btnReject: HTMLButtonElement = HtmlHelper.getElementById(ID_BTN_REJECT)
        btnAccept.onclick = {
            onDone(true)
        }
        btnReject.onclick = {
            onDone(false)
        }
    }

    fun applyModel(model: DisplayModel) {
        if (model.showCookieBanner) {
            jQuery(ctnCookie).show()
        } else {
            jQuery(ctnCookie).hide()
        }
    }
}
