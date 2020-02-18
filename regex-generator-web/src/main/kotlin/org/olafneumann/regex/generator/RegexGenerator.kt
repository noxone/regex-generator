package org.olafneumann.regex.generator

class RegexGenerator {
    fun recognize(input: String, config: Configuration = Configuration.default) =
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
    val generator = RegexGenerator()
    val findings = generator.recognize("2020-03-12T13:34:56.123+1 WARN  [org.olafneumann.test.Test]: This is #simple. A line with a 'string' in the text")
    val map = generator.organize(findings)

    findings.forEach { console.info(it) }
    map.forEach { console.info( "${it.key} -> ${it.value}") }
}


