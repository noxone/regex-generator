package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.DiffOperation
import org.olafneumann.regex.generator.regex.RecognizerMatch

class AugmentedRecognizerMatch(
    val original: RecognizerMatch,
    val first: Int = original.first,
    val length: Int = original.length
) {
    private val last = first + length

    override fun equals(other: Any?): Boolean {
        return original == other
    }

    fun <T> apply(diffOperation: DiffOperation<T>): AugmentedRecognizerMatch? =
        when (diffOperation) {
            is DiffOperation.Add -> {
                if (diffOperation.index < first) {
                    add(first = 1, length = 0)
                } else if (diffOperation.index == first) {
                    // TODO: What to do here?
                    console.log("Added at start of match")
                    null
                } else if (diffOperation.index in (first + 1) until last) {
                    add(first = 0, length = 1)
                } else {
                    this
                }
            }

            is DiffOperation.AddAll -> {
                if (diffOperation.index <= first) {
                    add(first = diffOperation.items.size, length = 0)
                } else if (diffOperation.index == first) {
                    // TODO: What to do here?
                    console.log("Added range at start of match")
                    null
                } else if (diffOperation.index in (first + 1) until last) {
                    add(first = 0, length = diffOperation.items.size)
                } else {
                    this
                }
            }

            is DiffOperation.Remove -> {
                if (diffOperation.index < first) {
                    add(first = -1, length = 0)
                } else if (diffOperation.index in first until last) {
                    add(first = 0, length = 1)
                } else {
                    this
                }
            }

            is DiffOperation.RemoveRange -> {
                if (diffOperation.endIndex < first) {
                    add(first = diffOperation.startIndex - diffOperation.endIndex, length = 0)
                } else if (diffOperation.startIndex > last) {
                    this
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

    private fun add(first: Int, length: Int): AugmentedRecognizerMatch? =
        if (this.length > length) {
            AugmentedRecognizerMatch(original = original, first = this.first + first, length = this.length + length)
        } else {
            null
        }
}