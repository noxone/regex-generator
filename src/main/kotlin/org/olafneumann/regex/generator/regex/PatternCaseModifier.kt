package org.olafneumann.regex.generator.regex

object PatternCaseModifier {
    private val CHARACTER_CLASS_REGEX = RegexCache.get("\\\\\\[|(\\[((?:[^\\\\]|\\\\.)*?)\\])")
    private val CHARACTER_SPAN_REGEX = RegexCache.get("\\\\[a-zA-Z]|([A-Za-z])(?:-([A-Za-z]))?")

    private const val EXPECTED_GROUP_COUNT_FOR_CLASS_REGEX = 3

    fun generateOutputPattern(pattern: String, case: Case): String {
        return pattern

        var out = pattern
        CHARACTER_CLASS_REGEX.findAll(pattern)
            .filter { it.groups.size == EXPECTED_GROUP_COUNT_FOR_CLASS_REGEX }
            .forEach { out = out.replace(it.groupValues[1], "[${convertClass(it.groupValues[2], case)}]") }
        return out
    }

    private fun convertClass(classString: String, case: Case): String {
        var out = classString
        val findings = CHARACTER_SPAN_REGEX.findAll(classString)
        findings
            .filter { it.groups.size > 1 }
            .map { analyzeSpan(it) }
            .filter { it.isSameCase }
            .fold(initial = mutableMapOf<String, MutableList<String>>()) { map, newItem ->
                map.getOrPut(newItem.toSpanString(case)) { mutableListOf() }
                    .add(newItem.original)
                map
            }
            .forEach { (replacement, needles) ->
                val indexOf = needles.minOf { out.indexOf(it) }
                needles.forEach { needle ->
                    out = out.replace(needle, "")
                }
                out = out.substring(0, indexOf) + replacement + out.substring(indexOf)
                //out += replacement
            }
        return out
    }

    private fun analyzeSpan(mr: MatchResult): Span {
        val group1 = mr.groupValues[1]
        val group2 = if (mr.groups.size > 2) {
            mr.groupValues[2].ifEmpty { null }
        } else {
            null
        }
        return Span(mr.groupValues[0], group1[0], group2?.get(0))
    }

    private data class Span(
        val original: String,
        val from: Char,
        val to: Char?
    ) {
        val isSingle: Boolean = to == null
        val isSameCase: Boolean = isSingle || from.isLowerCase() == to?.isLowerCase()

        private var rawSpanString: String = "$from${
            if (to != null) {
                "-$to"
            } else {
                ""
            }
        }"

        fun toSpanString(case: Case): String =
            listOf(
                if (case.uppercase) {
                    rawSpanString.uppercase()
                } else {
                    null
                },
                if (case.lowercase) {
                    rawSpanString.lowercase()
                } else {
                    null
                }
            )
                .mapNotNull { it }
                .distinct()
                .joinToString(separator = "")
    }

    enum class Case(
        val uppercase: Boolean,
        val lowercase: Boolean,
    ) {
        Upper(true, false),
        Lower(false, true),
        Both(true, true),
    }
}
