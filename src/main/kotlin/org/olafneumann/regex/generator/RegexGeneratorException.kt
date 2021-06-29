package org.olafneumann.regex.generator

open class RegexGeneratorException(
    override val message: String,
    override val cause: Exception? = null
) : Exception()
