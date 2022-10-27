package org.olafneumann.regex.generator.util

fun IntRange.hasIntersectionWith(other: IntRange): Boolean {
    return (this.first <= other.first && this.last >= other.first)
            || (other.first <= this.first && other.last >= this.first)
}
