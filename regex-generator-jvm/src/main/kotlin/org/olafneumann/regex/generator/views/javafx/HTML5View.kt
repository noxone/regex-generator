package org.olafneumann.regex.generator.views.javafx

import javafx.scene.web.WebView
import org.olafneumann.regex.generator.RGProtocol
import tornadofx.View
import java.net.URL
import java.nio.file.Paths

class HTML5View : View() {

    override val root = WebView()

    init {
        with(root) {
            setPrefSize(800.0, 600.0)
            // Atomatically set the title of the window as the HTML document title
            titleProperty.bind(engine.titleProperty())
            // Show all the contacts
            // engine.loadContent(contactsView(Contact.all()))

            RGProtocol.registerRegexGeneratorProtocol()
            engine.load(RGProtocol.toRegexGeneratorProtocol("index.html"))
        }
    }
}