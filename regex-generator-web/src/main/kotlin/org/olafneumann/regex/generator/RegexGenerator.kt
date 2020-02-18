package org.olafneumann.regex.generator

class RegexGenerator {
    fun recognize(input: String, config: Configuration = Configuration.default): List<RecognizerMatch> =
        config.recognizers
            .filter { it.active }
            .flatMap { it.searchRegex.findAll(input).map { mr -> RecognizerMatch(mr.range, extractMainGroup(mr, config), it) }.toList() }
            .sortedWith(compareBy( { it.range.first }, { it.range.last - it.range.first }, { it.recognizer.name } ))

    private fun extractMainGroup(matchResult: MatchResult, config: Configuration): String =
        when {
            config.mainGroupName != null -> (matchResult.groups as MatchNamedGroupCollection)[config.mainGroupName]?.value ?: throw Exception("Unable to find group '${config.mainGroupName}'")
            config.mainGroupIndex != null -> matchResult.groups[config.mainGroupIndex]?.value ?: throw Exception("Unable to find group with index ${config.mainGroupIndex}.")
            else -> matchResult.value
        }
}

fun main() {
    RegexGenerator().recognize("2020-12-24 INFO abg.def.h: Dies ist nur ein #beispiel! Und 3!")
        .forEach { console.info(it) }
}

