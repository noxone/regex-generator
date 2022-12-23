package org.olafneumann.regex.generator.util

class IdGenerator(
    initialValue: Int = 0
) {
    var next = initialValue
        get() { return ++field }
        private set
}
