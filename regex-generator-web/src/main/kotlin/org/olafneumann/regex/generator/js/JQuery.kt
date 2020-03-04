package org.olafneumann.regex.generator.js

import org.w3c.dom.HTMLElement

@JsName("$")
external fun jQuery(id: String): JQuery

fun jQuery(element: HTMLElement) = jQuery("#${element.id}")

external class JQuery() {
    fun on(type: String, callback: () -> Unit)
    fun show(): JQuery
    fun hide(): JQuery
    fun parent(): JQuery
    fun remove(): JQuery
    fun removeClass(className: String = definedExternally)
    fun addClass(className: String = definedExternally)
}