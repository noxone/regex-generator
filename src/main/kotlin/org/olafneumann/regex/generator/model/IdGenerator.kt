package org.olafneumann.regex.generator.model

class IdGenerator {
    var next = 0
        get() { return ++field }
        private set
}
