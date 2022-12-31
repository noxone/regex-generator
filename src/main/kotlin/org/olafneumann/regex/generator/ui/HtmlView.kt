package org.olafneumann.regex.generator.ui

import kotlinx.browser.window
import org.w3c.dom.url.URL

object HtmlView {
    const val CLASS_MATCH_ROW = "rg-match-row"
    const val CLASS_MATCH_ITEM = "rg-match-item"
    const val CLASS_ITEM_SELECTED = "rg-item-selected"
    const val CLASS_CHAR_SELECTED = "rg-char-selected"
    const val CLASS_ITEM_NOT_AVAILABLE = "rg-item-not-available"

    const val ID_ACTION_UNDO = "rg_button_action_undo"
    const val ID_ACTION_REDO = "rg_button_action_redo"
    const val ID_INPUT_ELEMENT = "rg_raw_input_text"
    const val ID_INPUT_MESSAGE_SHORTEN = "rg_raw_input_message_shorten"
    const val ID_INPUT_MESSAGE_SHORTEN_NUMBER = "rg_raw_input_message_shorten_number"
    const val ID_TEXT_DISPLAY = "rg_text_display"
    const val ID_RESULT_DISPLAY = "rg_result_display"
    const val ID_ROW_CONTAINER = "rg_row_container"
    const val ID_CHECK_ONLY_MATCHES = "rg_onlymatches"
    const val ID_CHECK_WHOLELINE = "rg_matchwholeline"
    const val ID_CHECK_CASE_INSENSITIVE = "rg_caseinsensitive"
    const val ID_CHECK_DOT_MATCHES_LINE_BRAKES = "rg_dotmatcheslinebreakes"
    const val ID_CHECK_MULTILINE = "rg_multiline"
    const val ID_BUTTON_COPY = "rg_button_copy"
    const val ID_BUTTON_HELP = "rg_button_show_help"
    const val ID_DIV_LANGUAGES = "rg_language_accordion"
    const val ID_ANCHOR_REGEX101 = "rg_anchor_regex101"
    const val ID_ANCHOR_REGEXR = "rg_anchor_regexr"
    const val ID_BUTTON_SHARE_LINK = "rg_button_copy_share_link"
    const val ID_TEXT_SHARE_LINK = "rg_result_link"

    const val SEARCH_SAMPLE_REGEX = "sampleText"
    const val SEARCH_FLAGS = "flags"
    const val SEARCH_SELECTION = "selection"
    const val SEARCH_CAP_GROUP = "capGroups"

    val MATCH_PRESENTER_CSS_CLASS = listOf("bg-primary", "bg-success", "bg-danger", "bg-warning")
    const val MAGIC_HEIGHT = 8
    const val HIDE_DELAY = 7000

    private val URL_CURRENT = URL(window.location.toString())
    fun URL.toCurrentWindowLocation(): URL {
        val url = URL(this.toString())
        url.protocol = URL_CURRENT.protocol
        url.hostname = URL_CURRENT.hostname
        url.port = URL_CURRENT.port
        return url
    }
}


