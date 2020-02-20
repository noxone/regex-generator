package org.olafneumann.regex.generator

import kotlin.browser.window

const val TEST_INPUT = "2020-03-12T13:34:56.123+1 WARN  [org.somepackage.test.Test]: This is #simple. A line with a 'string' in the text"

fun main() = initRegexGenerator()

private fun initRegexGenerator() {
    try {
        SimplePresenter().recognizeMatches()
    } catch (exception: Exception) {
        window.alert("Unable to initialize RegexGenerator: ${exception.message}")
    }
}
