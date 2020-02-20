package org.olafneumann.regex.generator

data class RecognizerMatch(
    val range: IntRange,
    val inputPart: String,
    val recognizer: Recognizer
) {
    private constructor(matchResult: MatchResult, inputPart: String, recognizer: Recognizer): this(matchResult.range, inputPart, recognizer)

    companion object {
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
    }

    override fun toString() = "[${range.first}+${range.last - range.first}] (${recognizer.name}: ${recognizer.outputPattern}) $inputPart"
}