package org.olafneumann.regex.generator.utils

fun IntRange.hasIntersectionWith(other: IntRange): Boolean {
    return (this.first <= other.first && this.last >= other.first)
            || (other.first <= this.first && other.last >= this.first)
}

val IntRange.length: Int get() = endInclusive - first + 1

val IntRange.center: Int get() = (first + last) / 2

fun IntRange.containsAndNotOnEdges(value: Int): Boolean =
    value > this.first && value < this.last

fun IntRange.containsAndNotOnEdges(value: IntRange): Boolean =
    containsAndNotOnEdges(value.first) && containsAndNotOnEdges(value.last)

fun IntRange.addPosition(index: Int): IntRange =
    if (index <= first) {
        IntRange(first + 1, last + 1)
    } else if (index <= last) {
        IntRange(first, last + 1)
    } else {
        this
    }


fun IntRange.add(rangeToAdd: IntRange): List<IntRange> {
    return when {
        rangeToAdd.first < this.first ->
            listOf(IntRange(this.first + rangeToAdd.length, this.last + rangeToAdd.length))

        rangeToAdd.first == this.first ->
            (rangeToAdd.first..(rangeToAdd.last + 1))
                .map { it..(this.last + rangeToAdd.length) }

        rangeToAdd.first > this.first && rangeToAdd.first <= this.last ->
            listOf(IntRange(this.first, this.last + rangeToAdd.length))

        rangeToAdd.first == this.last + 1 ->
                (0..rangeToAdd.length).map { this.first..(this.last + it) }

        else ->
            listOf(this)
    }
}

fun IntRange.remove(rangeToRemove: IntRange): List<IntRange> {
    return when {
        rangeToRemove.last < this.first ->
            listOf(IntRange(this.first - rangeToRemove.length, this.last - rangeToRemove.length))

        rangeToRemove.first > this.last ->
            listOf(this)

        rangeToRemove.first <= this.first && rangeToRemove.last >= this.last ->
            emptyList()

        rangeToRemove.first >= this.first && rangeToRemove.last <= this.last ->
            listOf(IntRange(this.first, this.last - rangeToRemove.length))

        rangeToRemove.first <= this.first && rangeToRemove.last > this.first ->
            listOf(IntRange(rangeToRemove.first, this.last - rangeToRemove.length))

        rangeToRemove.first <= this.last && rangeToRemove.last > this.last ->
            listOf(IntRange(this.first, rangeToRemove.first - 1))

        else ->
            listOf(this)
    }
}
