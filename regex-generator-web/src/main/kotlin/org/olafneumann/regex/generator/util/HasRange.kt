package org.olafneumann.regex.generator.util

interface HasRange {
    val first: Int
    val last: Int
    val length: Int
        get() = last - first + 1

    companion object {
        val comparator = compareBy<HasRange>({ it.first }, { -it.length })
    }
}