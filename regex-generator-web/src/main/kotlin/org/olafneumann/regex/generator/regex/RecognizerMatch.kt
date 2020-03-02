package org.olafneumann.regex.generator.regex

data class RecognizerMatch(
    val range: IntRange,
    val inputPart: String,
    val recognizer: Recognizer
) {
    companion object {

        fun recognize(input: String, config: RecognizerConfiguration = RecognizerConfiguration.default) =
            config.recognizers
                .filter { it.active }
                .flatMap { recognizer ->
                    recognizer.searchRegex.findAll(input)
                        .map { result ->
                            RecognizerMatch(
                                getMainGroupRange(result, config),
                                getMainGroupValue(result, config),
                                recognizer
                            )
                        }.toList()
                }
                .sortedWith(
                    compareBy(
                        { it.range.first },
                        { 0 - (it.range.last - it.range.first) },
                        { it.recognizer.name })
                )

        private fun getMainGroupValue(result: MatchResult, config: RecognizerConfiguration) =
            when {
                config.mainGroupName != null -> (result.groups as MatchNamedGroupCollection)[config.mainGroupName]?.value
                    ?: throw Exception("Unable to find group '${config.mainGroupName}'")
                config.mainGroupIndex != null -> result.groups[config.mainGroupIndex]?.value
                    ?: throw Exception("Unable to find group with index ${config.mainGroupIndex}.")
                else -> result.value
            }

        // the JS-Regex do not support positions for groups... so we need to use a quite bad work-around (that will not always work)
        private fun getMainGroupRange(result: MatchResult, config: RecognizerConfiguration): IntRange {
            val start = result.value.indexOf(getMainGroupValue(result, config))
            return IntRange(result.range.first + start, result.range.last + start)
        }
    }

    override fun toString() = "[${range.first}+${range.last - range.first}] (${recognizer.name}: ${recognizer.outputPattern}) $inputPart"
}