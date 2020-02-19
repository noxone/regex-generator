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

    fun organize(findings: List<RecognizerMatch>): List<MatchDisplay> {
        val lines = mutableListOf<Int>()
        return findings.map { finding ->
            val indexOfFreeLine = lines.indexOfFirst { it < finding.range.first }
            val line = if (indexOfFreeLine >= 0) indexOfFreeLine else { lines.add(0); lines.size - 1 }
            lines[line] = finding.range.last
            MatchDisplay(line, finding)
        }
    }

    data class MatchDisplay(
        val line: Int,
        val match: RecognizerMatch
    )
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

    map.forEach { console.info( "(${it.line}/${it.match.range.first}-${it.match.range.last}) ${it.match.inputPart} -> ${it.match.recognizer.name}") }
}


