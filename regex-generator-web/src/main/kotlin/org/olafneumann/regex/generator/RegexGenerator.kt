package org.olafneumann.regex.generator
import org.w3c.xhr.XMLHttpRequest

class RegexGenerator {
    fun recognize(config: Configuration = Configuration.default, input: String) =
        config.recognizers
            .filter { it.active }
            .flatMap { it.searchRegex.findAll(input).map { mr -> RecognizerMatch(mr.range, extractMainGroup(mr, config), it) }.toList() }
            .sortedWith(compareBy( { it.range.first }, { 0 - (it.range.last - it.range.first) }, { it.recognizer.name } ))

    private fun extractMainGroup(matchResult: MatchResult, config: Configuration) =
        when {
            config.mainGroupName != null -> (matchResult.groups as MatchNamedGroupCollection)[config.mainGroupName]?.value ?: throw Exception("Unable to find group '${config.mainGroupName}'")
            config.mainGroupIndex != null -> matchResult.groups[config.mainGroupIndex]?.value ?: throw Exception("Unable to find group with index ${config.mainGroupIndex}.")
            else -> matchResult.value
        }

    fun organize(findings: List<RecognizerMatch>): Map<RecognizerMatch, Int> {
        val lines = mutableListOf<Int>()
        fun getNextFreeLineAt(pos: Int) : Int {
            var index = lines.indexOfFirst { it < pos }
            if (index == -1) {
                index = lines.size
                lines.add(-1)
            }
            return index
        }
        fun enter(line: Int, end: Int): Int {
            lines[line] = end
            return line
        }
        return findings.map { it to enter(getNextFreeLineAt(it.range.first), it.range.last) }.toMap()
    }
}

fun main() {
//    loadFile("settings.json") {
//        val config = JSON.parse<Configuration>(it)
    val config = Configuration.default
    val input = "2020-03-12T13:34:56.123+1 WARN  [org.olafneumann.test.Test]: This is #simple. A line with a 'string' in the text"
    runGenerator(config, input)
//    }
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

fun runGenerator(config: Configuration, input: String) {
    val generator = RegexGenerator()
    val findings = generator.recognize(config = config, input = input)
    val map = generator.organize(findings)

    findings.forEach { console.info(it) }
    map.forEach { console.info( "${it.key} -> ${it.value}") }
}


