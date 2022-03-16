package org.olafneumann.regex.generator.regex

fun String.escapeForRegex() = this.replace(RegexCache.get("([|.\\\\^$\\[{}()*?+])"), "\\\\$1")
