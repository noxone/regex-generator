package org.olafneumann.regex.generator.model

@Suppress("EmptyClassBlock")
interface Model {
    val output: String

    @Suppress("EmptyClassBlock")
    interface Change {

    }
}

data class ModelWithDelta<M: Model, C: Model.Change>(
    val model: M,
    val change: C
)
