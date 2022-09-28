package org.olafneumann.regex.generator.model

@Suppress("EmptyClassBlock")
interface Model {
    @Suppress("EmptyClassBlock")
    interface Change {

    }
}

data class Result<M: Model, C: Model.Change>(
    val model: M,
    val change: C
)
