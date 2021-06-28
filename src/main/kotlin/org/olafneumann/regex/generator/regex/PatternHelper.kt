package org.olafneumann.regex.generator.regex

//object PatternHelper {
fun String.escapeForRegex() = this.replace(RegexCache.get("([.\\\\^$\\[{}()*?+])"), "\\$1")
//}