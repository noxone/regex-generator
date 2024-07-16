package org.olafneumann.regex.generator.ui.parts

import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.MVCContract
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLElement

class PXUserGuide(
    private val controller: MVCContract.Controller
) {
    private val buttonHelp: HTMLElement = HtmlHelper.getElementById(HtmlView.ID_BUTTON_HELP)

    init {
        buttonHelp.onclick = { controller.onShowUserGuide() }
    }
}
