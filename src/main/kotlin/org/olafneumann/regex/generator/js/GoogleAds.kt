package org.olafneumann.regex.generator.js

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList

object GoogleAds {
    private const val ADS_CLASSNAME = "adsbygoogle"
    private const val ADS_ATTRIBUTE_STATUS = "data-ad-status"
    private const val ADS_STATUS_FILLED = "filled"

    private const val CLASS_AD_HOLDER = "rg-ad-holder"
    private const val CLASS_START_AD_REMOVE = "rg-ads-remove-"

    private const val WAIT_TIMEOUT = 100

    fun install() {
        window.setTimeout({
          if (areAdsLoaded()) {
              activateAutoRefresh()
          } else {
              removeAdHolders()
          }
        }, WAIT_TIMEOUT)
    }

    private fun areAdsLoaded(): Boolean =
        getAdElements().any { it.googleAdFilled }

    private fun activateAutoRefresh() {
        window.addEventListener("resize", { refresh() })
    }

    private fun removeAdHolders() {
        getAdElements()
            .mapNotNull { htmlElement -> htmlElement.getParent { it.classList.contains(CLASS_AD_HOLDER) } }
            .forEach { element ->
                // check siblings for changes
                val next = element.nextElementSibling
                val classList = (next?.classList?.asList() ?: emptyList())
                    .filter { it.startsWith(CLASS_START_AD_REMOVE) }
                    .map { it.substring(CLASS_START_AD_REMOVE.length) }
                    .forEach { next?.classList?.remove(it) }


                // actually remove element
                element.remove()
            }
    }

    private fun HTMLElement.getParent(predicate: (Element) -> Boolean): Element? {
        var current = this.parentElement
        while (current != null && !predicate(current)) {
            current = current.parentElement
        }
        return current
    }

    private fun refresh() {
        try {
            js("(adsbygoogle = window.adsbygoogle || []).push({});")
        } catch (t: Throwable) {
            // don't care
            console.warn("Unable to refresh google ads", t)
        }
    }

    private fun getAdElements(): List<HTMLElement> =
        document.getElementsByClassName(ADS_CLASSNAME)
            .asList()
            .filterIsInstance<HTMLElement>()

    private val HTMLElement.googleAdFilled: Boolean
        get() = getAttribute(ADS_ATTRIBUTE_STATUS)?.equals(ADS_STATUS_FILLED) ?: false
}
