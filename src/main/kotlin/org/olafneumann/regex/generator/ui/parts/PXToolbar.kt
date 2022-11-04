package org.olafneumann.regex.generator.ui.parts

import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.MVCContract
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLButtonElement

class PXToolbar(
    private val controller: MVCContract.Controller
) {
    private val buttonUndo: HTMLButtonElement = HtmlHelper.getElementById(HtmlView.ID_ACTION_UNDO)
    private val buttonRedo: HTMLButtonElement = HtmlHelper.getElementById(HtmlView.ID_ACTION_REDO)

    init {
        buttonUndo.onclick = { controller.onUndo() }
        buttonRedo.onclick = { controller.onRedo() }
    }

    fun applyModel(model: DisplayModel) {
        enableUndoButton(model.isUndoAvailable)
        enableRedoButton(model.isRedoAvailable)
    }

    private fun enableUndoButton(enabled: Boolean) {
        buttonUndo.classList.toggle("disabled", !enabled)
    }

    private fun enableRedoButton(enabled: Boolean) {
        buttonRedo.classList.toggle("disabled", !enabled)
    }
}
