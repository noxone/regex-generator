package org.olafneumann.regex.generator

import org.w3c.dom.Document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document
import kotlin.browser.window

const val ID_INPUT_ELEMENT = "raw_input_text"

interface DisplayContract {
    companion object {
        fun organize(findings: List<RecognizerMatch>): List<MatchDisplay> {
            val lines = mutableListOf<Int>()
            return findings.map { finding ->
                val indexOfFreeLine = lines.indexOfFirst { it < finding.range.first }
                val line = if (indexOfFreeLine >= 0) indexOfFreeLine else { lines.add(0); lines.size - 1 }
                lines[line] = finding.range.last
                MatchDisplay(line, finding)
            }
        }
    }

    interface View {

    }

    interface Presenter {
        fun onInputChanges(newInput: String)
    }
}


class DisplayPage(
    private val presenter: DisplayContract.Presenter
) : DisplayContract.View {
    private val input = document.getElementById(ID_INPUT_ELEMENT) as HTMLInputElement

    init {
        input.addEventListener("input", {_->presenter.onInputChanges(input.value)})
    }
}

class SimplePresenter() : DisplayContract.Presenter {
    private val view: DisplayContract.View = DisplayPage(this)

    override fun onInputChanges(newInput: String) {
        runGenerator(newInput)
    }
}