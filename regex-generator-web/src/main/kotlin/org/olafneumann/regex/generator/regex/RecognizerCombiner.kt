package org.olafneumann.regex.generator.regex

import org.olafneumann.regex.generator.util.HasRange

class RecognizerCombiner {
    companion object {
        fun combine(
            inputText: String,
            selectedMatches: Collection<RecognizerMatch>,
            options: Options
        ): RegularExpression {
            val orderedMatches = selectedMatches.sortedWith(HasRange.comparator)

            val indices = mutableListOf<Int>(0)
            indices.addAll(orderedMatches
                .flatMap { it.ranges }
                .flatMap { listOf(it.first, it.last + 1) })
            indices.add(inputText.length)

            val staticValues = (0 until indices.size step 2)
                .asSequence()
                .map { indices[it] to indices[it + 1] }
                .map { inputText.substring(it.first, it.second) }
                .map { it.escapeForRegex() }
                .map { if (options.onlyPatterns && it.isNotEmpty()) ".*" else it }
                .toMutableList()

            val start: String
            val end: String
            if (options.matchWholeLine) {
                start = "^"
                end = "$"
            } else {
                start = ""
                end = ""
                staticValues[0] = ""
                staticValues[staticValues.size - 1] = ""
            }

            val pattern =
                staticValues.reduceIndexed { index, acc, s -> "${acc}${if ((index - 1) < orderedMatches.size) orderedMatches[index - 1].recognizer.outputPattern else ""}${s}" }

            return RegularExpression("$start$pattern$end")
        }

        private fun String.escapeForRegex() = replace(Regex("([.\\\\^$\\[{}()*?+])"), "\\$1")
    }

    data class Options(
        val onlyPatterns: Boolean = false,
        val matchWholeLine: Boolean = true,
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