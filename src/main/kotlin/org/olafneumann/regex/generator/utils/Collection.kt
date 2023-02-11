package org.olafneumann.regex.generator.utils

fun Collection<Any>.toIndexedString(prefix: String = ""): String = asSequence().toIndexedString(prefix = prefix)

fun <T> Sequence<T>.toIndexedString(prefix: String = ""): String =
    mapIndexed { index, recognizerMatch -> index to recognizerMatch }
        .joinToString(separator = "\n", prefix = prefix) { "${it.first}: ${it.second}" }

fun <T> Sequence<T>.peek(action: (T) -> Unit) = map { action(it); it }
