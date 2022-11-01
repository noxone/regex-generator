package org.olafneumann.regex.generator.util

fun IntRange.hasIntersectionWith(other: IntRange): Boolean {
    return (this.first <= other.first && this.last >= other.first)
            || (other.first <= this.first && other.last >= this.first)
}

val IntRange.length: Int get() = endInclusive - first + 1

fun IntRange.containsAndNotOnEdges(value: Int): Boolean =
    value > this.first && value < this.last

fun IntRange.containsAndNotOnEdges(value: IntRange): Boolean =
    containsAndNotOnEdges(value.first) && containsAndNotOnEdges(value.last)

fun IntRange.add(rangeToAdd: IntRange): List<IntRange> {
    return if (rangeToAdd.first < this.first) {
        listOf(IntRange(this.first + rangeToAdd.length, this.last + rangeToAdd.length))
    } else if (rangeToAdd.first == this.first) {
        emptyList()
    } else if (rangeToAdd.first > this.first && rangeToAdd.first <= this.last) {
        listOf(IntRange(this.first, this.last + rangeToAdd.length))
    } else if (rangeToAdd.first == this.last + 1) {
        emptyList()
    } else {
        listOf(this)
    }
}

fun IntRange.remove(rangeToRemove: IntRange): List<IntRange> {
    return if (rangeToRemove.last < this.first) {
        listOf(IntRange(this.first - rangeToRemove.length, this.last - rangeToRemove.length))
    } else if (rangeToRemove.first > this.last) {
        listOf(this)
    } else if (rangeToRemove.first <= this.first && rangeToRemove.last >= this.last) {
        emptyList()
    } else if (rangeToRemove.first >= this.first && rangeToRemove.last <= this.last) {
        listOf(IntRange(this.first, this.last - rangeToRemove.length))
    } else if (rangeToRemove.first <= this.first && rangeToRemove.last > this.first) {
        listOf(IntRange(rangeToRemove.first, this.last - rangeToRemove.length))
    } else if (rangeToRemove.first <= this.last && rangeToRemove.last > this.last) {
        listOf(IntRange(this.first, rangeToRemove.first - 1))
    } else {
        listOf(this)
    }
}
