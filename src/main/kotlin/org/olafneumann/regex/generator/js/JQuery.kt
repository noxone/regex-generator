package org.olafneumann.regex.generator.js

import org.w3c.dom.HTMLElement
import kotlin.js.Json

@JsName("$")
external fun jQuery(id: String): JQuery
@JsName("$")
external fun jQuery(element: HTMLElement): JQuery

@Suppress("TooManyFunctions")
external class JQuery {
    @Suppress("UnusedPrivateMember")
    fun on(type: String, callback: () -> Unit)
    fun hide(): JQuery
    fun parent(): JQuery
    fun remove(): JQuery

    @Suppress("UnusedPrivateMember")
    fun find(selector: String): JQuery

    @Suppress("UnusedPrivateMember")
    fun attr(name: String, newValue: Any = definedExternally): Any?
    @Suppress("UnusedPrivateMember")
    fun css(css: Any)
    fun height(): Int
    @Suppress("UnusedPrivateMember")
    fun each(function: ((Int, HTMLElement) -> Unit))

    @Suppress("UnusedPrivateMember")
    fun animate(properties: Json, duration: Int = definedExternally, easing: String = definedExternally)
    fun stop()
}
