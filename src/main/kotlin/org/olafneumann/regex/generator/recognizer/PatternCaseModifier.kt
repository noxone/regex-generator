package org.olafneumann.regex.generator.recognizer

object PatternCaseModifier {
    private val CHARACTER_CLASS_REGEX = Regex("\\[(.*?)\\]")
    private val CHARACTER_SPAN_REGEX = Regex("((?<!\\\\)[A-Za-z])(?:-([A-Za-z]))?")

    fun generateOutputPattern(pattern: String, case: Case): String {
        var out = pattern
        CHARACTER_CLASS_REGEX.findAll(pattern)
            .forEach { out = out.replace(it.groupValues[0], "[${convertClass(it.groupValues[1], case)}]") }
        return out
    }

    private fun convertClass(classString: String, case: Case): String {
        var out = classString
        CHARACTER_SPAN_REGEX.findAll(classString)
            .map { analyzeSpan(it).toSpanString(case) to it.groupValues[0] }
            .fold(initial = mutableMapOf<String, MutableList<String>>()) { map, newItem ->
                map.getOrPut(newItem.first) { mutableListOf() }
                    .add(newItem.second)
                map
            }
            .forEach { (replacement, needles) ->
                needles.forEach { needle ->
                    out = out.replace(needle, "")
                }
                out += replacement
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
        return Span(group1[0].uppercaseChar(), group2?.get(0)?.uppercaseChar())
    }

    private data class Span(
        val from: Char,
        val to: Char?
    ) {
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
