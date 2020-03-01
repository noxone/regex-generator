package org.olafneumann.regex.generator.js

import kotlin.js.Promise

external object navigator : Navigator

open external class Navigator {
    val clipboard: Clipboard
}
external class Clipboard {
    fun writeText(text: String): Promise<Any>
}

