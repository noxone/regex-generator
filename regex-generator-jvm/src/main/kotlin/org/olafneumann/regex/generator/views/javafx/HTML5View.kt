package org.olafneumann.regex.generator.views.javafx

import javafx.scene.web.WebView
import org.olafneumann.regex.generator.RegexGeneratorProtocol
import tornadofx.View

class HTML5View : View() {

    override val root = WebView()

    init {
        printJavaInformation()

        with(root) {
            setPrefSize(800.0, 600.0)
            // Automatically set the title of the window as the HTML document title
            titleProperty.bind(engine.titleProperty())
            // Show index.html from web module
            engine.load(RegexGeneratorProtocol.toRegexGeneratorProtocol("index.html"))
        }
    }

    private fun printJavaInformation() {
        println("Java version:   ${System.getProperty("java.runtime.version")}")
        println("JavaFX version: ${System.getProperty("javafx.runtime.version")}")
        println("OS:             ${System.getProperty("os.name")} (${System.getProperty("os.arch")})")
        println("User agent:     ${root.engine.userAgent}")
    }

}