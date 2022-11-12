package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.regex.Options

interface CodeGenerator {
    companion object {
        val all = listOf<CodeGenerator>(
            CRegexCodeGenerator()
            , CSharpCodeGenerator()
            , GoCodeGenerator()
            , GrepCodeGenerator()
            , JavaCodeGenerator()
            , JavaScriptCodeGenerator()
            , KotlinCodeGenerator()
            , PhpCodeGenerator()
            , PythonCodeGenerator()
            , RubyCodeGenerator()
            , SwiftCodeGenerator()
            , VisualBasicNetCodeGenerator()
        ).sortedBy { it.languageName.lowercase() }

        private val String.codePointString: String
            get() {
                return if (isNotEmpty()) {
                    "_u${this[0].code}_"
                } else {
                    ""
                }
            }

        internal val String.htmlIdCompatible: String
            get() = this
                .replace(" ", "__")
                .replace("-", "_minus_")
                .replace(".", "_dot_")
                .replace("+", "_plus_")
                .replace("#", "_sharp_")
                .replace(regex = Regex("[^_A-Za-z0-9]"), transform = { it.value.codePointString })
    }

    val languageName: String

    val highlightLanguage: String

    val uniqueName: String
        get() = languageName.htmlIdCompatible

    fun generateCode(pattern: String, options: Options): GeneratedSnippet

    data class GeneratedSnippet(
        val snippet: String,
        val warnings: List<String> = emptyList()
    )
}
