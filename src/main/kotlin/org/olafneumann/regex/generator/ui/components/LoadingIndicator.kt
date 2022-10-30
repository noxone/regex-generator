package org.olafneumann.regex.generator.ui.components

import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLDivElement

object LoadingIndicator {
    private const val ID_DIV_LOADING = "rg_loading"

    private val ctnLoading: HTMLDivElement = HtmlHelper.getElementById(ID_DIV_LOADING)

    fun hideLoading() {
        jQuery(ctnLoading).remove()//.hide()
    }
}
