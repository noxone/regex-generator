package org.olafneumann.regex.generator.model

import org.olafneumann.regex.generator.regex.RecognizerMatch

class AugmentedRecognizerMatch {
    private val original: RecognizerMatch

    constructor(match: RecognizerMatch) {
        this.original = match
    }
}