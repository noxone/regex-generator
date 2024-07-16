package org.olafneumann.regex.generator.utils

class IdGenerator(
    initialValue: Int = 0
) {
    var next = initialValue
        get() { return ++field }
        private set
}
