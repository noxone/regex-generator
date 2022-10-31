package org.olafneumann.regex.generator.ui.parts

import org.olafneumann.regex.generator.model.DisplayModel
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

    init {
        buttonShareLink.onclick = { controller.onShareButtonClick() }
    }

    /*private val textShare = TextHandler(
        HtmlHelper.getElementById(HtmlView.ID_TEXT_SHARE_LINK),
        UrlGenerator("ShareLink", "https://regex-generator.olafneumann.org/?sampleText=%1\$s&flags=%2\$s")
    )*/

    fun applyModel(model: DisplayModel) {
        divShareLink.innerText = model.shareLink
    }
}
