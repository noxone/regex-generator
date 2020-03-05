package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.RecognizerMatch
import kotlin.properties.Delegates

class RecognizerMatchPresentation(
    val recognizerMatch: RecognizerMatch,
    selected: Boolean = false,
    deactivated: Boolean = false,
    var onSelectedChanged: (Boolean) -> Unit = {},
    var onDeactivatedChanged: (Boolean) -> Unit = {}
) {
    var selected: Boolean by Delegates.observable(selected)
    { _, _, new -> onSelectedChanged(new) }
    var deactivated: Boolean by Delegates.observable(deactivated)
    { _, _, new -> onDeactivatedChanged(new) }

    val availableForHighlight: Boolean = !deactivated
}