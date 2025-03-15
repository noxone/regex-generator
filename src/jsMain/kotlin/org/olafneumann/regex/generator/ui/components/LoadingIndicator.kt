package org.olafneumann.regex.generator.ui.components

import org.olafneumann.regex.generator.js.asJQuery
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLDivElement
import kotlin.js.json

class LoadingIndicator {
    companion object {
        private const val ID_DIV_LOADING = "rg_loading"
        private const val ANIMATION_DURATION = 800
    }

    private val ctnLoading: HTMLDivElement = HtmlHelper.getElementById(ID_DIV_LOADING)

    private var done = false

    fun applyModel(model: DisplayModel) {
        if (done) {
            return
        }

        if (!model.showLoadingIndicator) {
            hideLoading()
            done = true
        }
    }

    private fun hideLoading() {
        ctnLoading.asJQuery()
            .fadeOut(json("duration" to ANIMATION_DURATION, "complete" to { ctnLoading.asJQuery().remove() }))
    }
}
