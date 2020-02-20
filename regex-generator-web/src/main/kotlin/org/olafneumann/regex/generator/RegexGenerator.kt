package org.olafneumann.regex.generator
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window

const val LOAD_CONFIG = false
const val TEST_INPUT = "2020-03-12T13:34:56.123+1 WARN  [org.olafneumann.test.Test]: This is #simple. A line with a 'string' in the text"

fun main() {
}

fun initRegexGenerator() {
    try {
        SimplePresenter()
    } catch (exception: Exception) {
        window.alert("Unable to initialize RegexGenerator: ${exception.message}")
    }
}

private fun loadFile(url: String, callback: (String) -> Unit) {
    val xmlHttp = XMLHttpRequest()
    xmlHttp.open("GET", url)
    xmlHttp.onload = {
        if (xmlHttp.readyState == 4.toShort() && xmlHttp.status == 200.toShort()) {
            callback.invoke(xmlHttp.responseText)
        }
    }
    xmlHttp.send()
}

fun runGenerator(input: String, config: Configuration = Configuration.default) {
    val findings = RecognizerMatch.recognize(config = config, input = input)

    val map = DisplayContract.organize(findings)

    map.forEach { console.info( "(${it.line}/${it.match.range.first}-${it.match.range.last}) ${it.match.inputPart} -> ${it.match.recognizer.name}") }
}


