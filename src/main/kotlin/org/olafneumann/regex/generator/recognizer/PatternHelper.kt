package org.olafneumann.regex.generator.recognizer

import org.olafneumann.regex.generator.regex.RegexCache

fun String.escapeForRegex() = this.replace(RegexCache.get("([|.\\\\^$\\[{}()*?+])"), "\\\\$1")
