package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.DiffOperation
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.util.HasRanges

class AugmentedRecognizerMatch private constructor(
    val original: RecognizerMatch,
    val first: Int = original.first,
    val length: Int = original.length
) : HasRanges {
    companion object {
        fun enhancing(original: RecognizerMatch): AugmentedRecognizerMatch? {
            return if (original.ranges.size == 1) {
                AugmentedRecognizerMatch(original = original)
            } else {
                // TODO also enable matches with several ranges
                null
            }
        }
    }

    private val last = first + length
    override val ranges: List<IntRange>
        get() = listOf(IntRange(start = first, endInclusive = last - 1))

    override fun equals(other: Any?): Boolean {
        val out = when (other) {
            null -> false
            is AugmentedRecognizerMatch ->
                original.recognizer == other.original.recognizer
                        && original.title == other.original.title
                        && original.patterns == other.original.patterns
                        && this.hasSameRangesAs(other)

            is RecognizerMatch ->
                original.recognizer == other.recognizer
                        && original.title == other.title
                        && original.patterns == other.patterns
                        && this.hasSameRangesAs(other)

            else -> false
        }
        console.log(out.toString(), toString(), "Equals", other)
        return out
    }


    override fun hashCode(): Int {
        var result = original.recognizer.hashCode()
        result = 31 * result + original.patterns.hashCode()
        result = 31 * result + original.title.hashCode()
        result = 31 * result + ranges.hashCode()
        return result
    }

    private fun <T> apply(diffOperation: DiffOperation<T>): AugmentedRecognizerMatch? =
        when (diffOperation) {
            is DiffOperation.Add -> {
                if (diffOperation.index < first) {
                    add(first = 1)
                } else if (diffOperation.index == first) {
                    // TODO: What to do here?
                    console.log("Added at start of match")
                    null
                } else if (diffOperation.index in (first + 1) until last) {
                    add(length = 1)
                } else {
                    this
                }
            }

            is DiffOperation.AddAll -> {
                if (diffOperation.index <= first) {
                    add(first = diffOperation.items.size)
                } else if (diffOperation.index == first) {
                    // TODO: What to do here?
                    console.log("Added range at start of match")
                    null
                } else if (diffOperation.index in (first + 1) until last) {
                    add(length = diffOperation.items.size)
                } else {
                    this
                }
            }

            is DiffOperation.Remove -> {
                if (diffOperation.index < first) {
                    add(first = -1)
                } else if (diffOperation.index in first until last) {
                    add(length = -1)
                } else {
                    this
                }
            }

            is DiffOperation.RemoveRange -> {
                if (diffOperation.endIndex < first) {
                    add(first = diffOperation.startIndex - diffOperation.endIndex)
                } else if (diffOperation.startIndex > last) {
                    this
                } else if (diffOperation.startIndex >= first && diffOperation.endIndex < last) {
                    add(length = diffOperation.startIndex - diffOperation.endIndex)
                } else {
                    // remove this
                    null
                } /*else if (diffOperation.startIndex <= first && diffOperation.endIndex < last) {
                    add(first = first - diffOperation.startIndex, length = diffOperation.endIndex - first)
                } else if (diffOperation.startIndex <= first && diffOperation.endIndex >= last) {
                    null
                } else if (diffOperation.startIndex >= first && diffOperation.)*/
            }

            else -> throw RuntimeException("Unknown or unexpected Diff Operation: $this")
        }

    fun <T> applyAll(diffOperations: List<DiffOperation<T>>?): AugmentedRecognizerMatch? {
        var out: AugmentedRecognizerMatch? = this
        diffOperations?.forEach { out = out?.apply(it) }
        return out
    }

    private fun add(first: Int = 0, length: Int = 0): AugmentedRecognizerMatch? =
        if (this.first >= first && this.length > length) {
            AugmentedRecognizerMatch(original = original, first = this.first + first, length = this.length + length)
        } else {
            null
        }

    override fun toString(): String {
        return "AugmentedRecognizerMatch(title=${original.title}, position=$first/$length, priority=${original.priority}, recognizer=${original.recognizer.name}, patterns=${original.patterns})"
    }
}
