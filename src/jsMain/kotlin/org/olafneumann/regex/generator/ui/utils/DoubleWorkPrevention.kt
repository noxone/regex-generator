package org.olafneumann.regex.generator.ui.utils

class DoubleWorkPrevention<T>(
    private val action: (T?) -> Unit
) {
    private var item: T? = null

    fun set(item: T?) {
        if (this.item != item) {
            this.item = item
            action(item)
        }
    }
}
