package org.olafneumann.regex.generator

class RegexGenerator {
    fun recognize(input: String, config: Configuration = Configuration.config): List<RecognizerMatch> {
        return config.recognizers
                .filter { it.active }
                .flatMap { findPatternProposals(input, it) }
                .sortedWith(compareBy( { it.range.first }, { it.range.last - it.range.first }, { it.recognizer.name } ))
    }

    private fun findPatternProposals(input: String, recognizer: Recognizer): List<RecognizerMatch> {
        return recognizer.searchRegex
                .findAll(input)
                .map { RecognizerMatch(it.range, (it.groups as MatchNamedGroupCollection).let { gc -> gc["main"]?.value ?: throw Exception("Cannot find main group") }, recognizer) }
                .toList()
    }
}

fun main() {
    console.info(RegexGenerator().recognize("2020-12-24 INFO abg.def.h: Dies ist nur ein #beispiel!"))
}

