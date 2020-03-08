package org.olafneumann.regex.generator.regex

object PatternHelper {
    fun escapeForRegex(input: String) = input.replace(Regex("([.\\\\^$\\[{}()*?+])"), "\\$1")
}