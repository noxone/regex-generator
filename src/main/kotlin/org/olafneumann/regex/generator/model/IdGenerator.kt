package org.olafneumann.regex.generator.model

class IdGenerator {
    var currentId: Int = 0
        get() = field++
        private set
}
