package org.olafneumann.regex.generator.ui.parts

import kotlinx.browser.document
import kotlinx.browser.window
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.MVCContract
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement

class PXShare(
    private val controller: MVCContract.Controller
) {
    private val divShareLink = HtmlHelper.getElementById<HTMLDivElement>(HtmlView.ID_TEXT_SHARE_LINK)
    private val buttonShareLink = HtmlHelper.getElementById<HTMLButtonElement>(HtmlView.ID_BUTTON_SHARE_LINK)
    private val imageShareElement = buttonShareLink.firstElementChild!!

    init {
        buttonShareLink.onclick = { controller.onShareButtonClick { showCheckMark() } }
    }

    fun applyModel(model: DisplayModel) {
        showOrHideCopyButton(model.showCopyButton)
        val url = model.shareLink

        divShareLink.innerText = url.toString()
        updateDocumentSearchQuery(url.search)
    }

    private fun updateDocumentSearchQuery(urlString: String) {
        window.history.replaceState(data = null, title = document.title, url = urlString)
    }

    private fun showCheckMark(show: Boolean = true) {
        imageShareElement.classList.toggle(CLASS_ICON_COPY, !show)
        imageShareElement.classList.toggle(CLASS_ICON_CHECK, show)
        if (show) {
            window.setTimeout({ showCheckMark(false) }, ANIMATION_DURATION)
        }
    }

    private fun showOrHideCopyButton(show: Boolean) {
        buttonShareLink.classList.toggle("d-none", !show)
    }

    companion object {
        private const val CLASS_ICON_COPY = "bi-clipboard"
        private const val CLASS_ICON_CHECK = "bi-check-lg"

        private const val ANIMATION_DURATION = 2000
    }
}
