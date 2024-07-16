package org.olafneumann.regex.generator.js

object NPM {
    fun importAll() {
        importDriverJs()
    }

    private fun importDriverJs() {
        kotlinext.js.require("driver.js/dist/driver.min.css")
    }
}
