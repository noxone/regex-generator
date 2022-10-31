package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.model.DisplayModel
import org.olafneumann.regex.generator.regex.RecognizerMatch

interface MVCContract {
    interface View {
        fun showModel(model: DisplayModel)
    }

    interface Controller {
        fun setUserInput(input: String)
        fun onClick(recognizerMatch: RecognizerMatch)
    }
}