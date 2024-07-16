package org.olafneumann.regex.generator.js

import kotlin.js.Json
import kotlin.js.json

fun Map<String, Any?>.toJson(): Json = json(pairs = this
    .filter { it.value != null }
    .map { it.key to it.value }
    .toTypedArray())
