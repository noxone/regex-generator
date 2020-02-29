package org.olafneumann.regex.generator.regex

class RecognizerCombiner {
    companion object {
        fun combine(inputText: String, selectedMatches: Collection<RecognizerMatch>, options: Options = Options()): RegularExpression {
            val orderedMatches = selectedMatches.sortedBy { it.range.first }

            val indices = mutableListOf<Int>(0)
            indices.addAll(orderedMatches.flatMap { listOf(it.range.first, it.range.last + 1) })
            indices.add(inputText.length)

            val staticValues = (0 until indices.size step 2)
                .map { indices[it] to indices[it + 1] }
                .map { inputText.substring(it.first, it.second) }
                .map { escapeRegex(it) }
                .map { if (options.onlyPatterns && it.isNotEmpty()) ".*" else it }

            val pattern = staticValues.reduceIndexed { index, acc, s -> "${acc}${if ((index - 1) < orderedMatches.size) orderedMatches[index - 1].recognizer.outputPattern else ""}${s}" }

                //.reduceIndexed { index, acc, item -> "${acc}${if ((index - 1) < orderedMatches.size) orderedMatches[index - 1].recognizer.outputPattern else ""}${item}"}

            return RegularExpression("^${pattern}$")
        }

        private fun escapeRegex(input: String) = input.replace(Regex("([.\\\\^$\\[{}()*?+])"),"\\$1")
    }

    data class Options(
        val onlyPatterns: Boolean = false,
        val caseSensitive: Boolean = true,
        val dotMatchesLineBreaks: Boolean = false,
        val multiline: Boolean = false
    )

    data class RegularExpression(
        val pattern: String
    ) {
        val regex: Regex by lazy { Regex(pattern) }
    }
}