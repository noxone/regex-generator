package org.olafneumann.regex.generator.ui.parts

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.request.get
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.olafneumann.regex.generator.js.Driver
import org.olafneumann.regex.generator.js.StepDefinition

class UserGuide private constructor(
    private val language: String
) {
    companion object {
        private val languageToGuide = mutableMapOf<String, UserGuide>()
        fun forLanguage(language: String = "en") = languageToGuide.getOrPut(language) { UserGuide(language) }
    }

    private val driver = Driver(js("{className: 'rg-guide'}"))
    private var stepDefinitions: Array<StepDefinition>? = null

    fun show(initialStep: Boolean) {
        if (stepDefinitions == null) {
            CoroutineScope(Dispatchers.Unconfined).launch {
                loadUserGuide()
                displayUserGuide(initialStep)
            }
        } else {
            displayUserGuide(initialStep)
        }
    }

    private suspend fun loadUserGuide() {
        val client = HttpClient(Js)
        val stepsString: String = client.get(url = userGuideSourceUrl).body()
        stepDefinitions = JSON.parse<Array<StepDefinition>>(stepsString)
    }

    private val userGuideSourceUrl: Url
        get() = Url(userGuideSourceFilename)
    private val userGuideSourceFilename: String
        get() = "text/user-guide.${language}.json"

    private fun displayUserGuide(initialStep: Boolean) {
        driver.reset()
        driver.defineSteps(stepDefinitions!!)
        driver.start(if (initialStep) 0 else 1)
    }
}
