package org.olafneumann.regex.generator.recognizer

import org.olafneumann.regex.generator.regex.PatternCaseModifier
import org.olafneumann.regex.generator.regex.RegexCache
import org.olafneumann.regex.generator.regex.PatternCaseModifier.Case

class SimpleRecognizer(
    override val name: String,
    private val outputPrePattern: String,
    private val searchPattern: String = "(${PatternCaseModifier.generateOutputPattern(outputPrePattern, Case.Both)})",
    private val mainGroupIndex: Int = 1,
    override val description: String? = null,
    override val isDerived: Boolean = false,
) : Recognizer {
    private val searchRegex = RegexCache.get(searchPattern.replace("%s", getOutputPattern(Case.Both)))

    private fun getOutputPattern(case: Case): String =
        PatternCaseModifier.generateOutputPattern(outputPrePattern, case)

    override fun findMatches(input: String): List<RecognizerMatch> =
        searchRegex.findAll(input)
            .map { result ->
                RecognizerMatch(
                    patterns = listOf(getOutputPattern(Case.Both)),
                    ranges = listOf(getMainGroupRange(result)),
                    recognizer = this,
                    title = name
                )
            }.toList()

    private fun getMainGroupValue(result: MatchResult) =
        result.groups[mainGroupIndex]?.value
            ?: throw RecognizerException("No main group defined in search pattern: ${searchPattern}.")

    // the JS-Regex do not support positions for groups...
    // so we need to use a quite bad work-around (that will not always work)
    private fun getMainGroupRange(result: MatchResult): IntRange {
        val mainGroupValue = getMainGroupValue(result)
        val start = result.value.indexOf(mainGroupValue)
        return IntRange(
            start = result.range.first + start,
            endInclusive = result.range.first + start + mainGroupValue.length - 1
        )
    }

    override fun equals(other: Any?) = other is SimpleRecognizer
            && this.name == other.name
            && this.outputPrePattern == other.outputPrePattern
            && this.searchPattern == other.searchPattern

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + outputPrePattern.hashCode()
        result = 31 * result + searchPattern.hashCode()
        return result
    }
}
