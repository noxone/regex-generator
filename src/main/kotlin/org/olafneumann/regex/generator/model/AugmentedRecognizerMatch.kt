package org.olafneumann.regex.generator.model

import org.olafneumann.regex.generator.regex.RecognizerMatch

class AugmentedRecognizerMatch {
    private val original: RecognizerMatch

    constructor(match: RecognizerMatch) {
        this.original = match
    }


    val first: Int get() = original.first
    val length: Int get() = original.length

    override fun equals(other: Any?): Boolean {
        return original == other
    }
}