package org.olafneumann.views.javafx

import org.olafneumann.models.Contact
import org.olafneumann.views.html.contactsView
import javafx.scene.web.WebView
import tornadofx.View

class HTML5View : View() {
    companion object {
        fun resourceLink(path: String) = "${HTML5View::class.java.getResource(path)}"
    }

    override val root = WebView()

    init {
        with(root) {
            setPrefSize(800.0, 600.0)
            // Atomatically set the title of the window as the HTML document title
            titleProperty.bind(engine.titleProperty())
            // Show all the contacts
            engine.loadContent(contactsView(Contact.all()))
        }
    }
}