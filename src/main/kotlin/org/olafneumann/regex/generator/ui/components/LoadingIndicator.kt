package org.olafneumann.regex.generator.ui.components

import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.model.DisplayModel
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLDivElement

class LoadingIndicator {
    companion object {
        private const val ID_DIV_LOADING = "rg_loading"
    }

    private val ctnLoading: HTMLDivElement = HtmlHelper.getElementById(ID_DIV_LOADING)

    private var done = false

    fun applyModel(model: DisplayModel) {
        if (done) { return }

        if (!model.showLoadingIndicator) {
            hideLoading()
            done = true
        }
    }

    private fun hideLoading() {
        jQuery(ctnLoading).remove()
    }
}
