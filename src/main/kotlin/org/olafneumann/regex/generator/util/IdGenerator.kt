package org.olafneumann.regex.generator.util

class IdGenerator {
    var next = 0
        get() { return ++field }
        private set
}
