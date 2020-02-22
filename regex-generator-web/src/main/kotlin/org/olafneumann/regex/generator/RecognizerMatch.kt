package org.olafneumann.regex.generator

data class RecognizerMatch(
    val range: IntRange,
    val inputPart: String,
    val recognizer: Recognizer
) {
    companion object {

        fun recognize(input: String, config: Configuration = Configuration.default) =
            config.recognizers
                .filter { it.active }
                .flatMap { recognizer -> recognizer.searchRegex.findAll(input).map { mr -> RecognizerMatch(mr.range, mr.getMainGroup(config), recognizer) }.toList() }
                .sortedWith(compareBy( { it.range.first }, { 0 - (it.range.last - it.range.first) }, { it.recognizer.name } ))

        private fun MatchResult.getMainGroup(config: Configuration)=
            when {
                config.mainGroupName != null -> (groups as MatchNamedGroupCollection)[config.mainGroupName]?.value ?: throw Exception("Unable to find group '${config.mainGroupName}'")
                config.mainGroupIndex != null -> groups[config.mainGroupIndex]?.value ?: throw Exception("Unable to find group with index ${config.mainGroupIndex}.")
                else -> value
            }
    }

    override fun toString() = "[${range.first}+${range.last - range.first}] (${recognizer.name}: ${recognizer.outputPattern}) $inputPart"
}