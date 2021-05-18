package org.olafneumann.regex.generator.js

import org.w3c.dom.HTMLElement
import kotlin.js.Json

@JsName("$")
external fun jQuery(id: String): JQuery
@JsName("$")
external fun jQuery(element: HTMLElement): JQuery

external class JQuery {
    fun on(type: String, callback: () -> Unit)
    fun hide(): JQuery
    fun parent(): JQuery
    fun remove(): JQuery

    fun find(selector: String): JQuery

    fun attr(name: String, newValue: Any = definedExternally): Any?
    fun css(css: Any)
    fun height(): Int
    fun each(function: ((Int, HTMLElement) -> Unit))

    fun animate(properties: Json, duration: Int = definedExternally, easing: String = definedExternally)
    fun stop()
}