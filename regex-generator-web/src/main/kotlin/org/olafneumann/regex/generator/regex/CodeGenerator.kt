package org.olafneumann.regex.generator.regex

interface CodeGenerator {
    companion object {
        val list = listOf(
            JavaCodeGenerator()/*,
            KotlinCodeGenerator()*/
        )
    }

    val languageName: String

    fun generateCode(pattern: String, options: RecognizerCombiner.Options): String

    abstract class SimpleReplacingCodeGenerator : CodeGenerator {
        abstract fun escapePattern(pattern: String): String

        abstract val templateCode: String

        abstract fun generateOptionsCode(options: RecognizerCombiner.Options): String

        override fun generateCode(pattern: String, options: RecognizerCombiner.Options): String =
            templateCode.replace("%1${'$'}s", escapePattern(pattern))
                .replace("%2${'$'}s", generateOptionsCode(options))
    }

    abstract class CLikeCodeGenerator() : SimpleReplacingCodeGenerator() {
        override fun escapePattern(pattern: String): String =
            pattern.replace(Regex("([\\\\\"])"), "\\$1")
    }

    class JavaCodeGenerator() : CLikeCodeGenerator() {
        override val languageName: String get() = "Java"

        override val templateCode: String
            get() = """import java.util.regex.Pattern;
                |import java.util.regex.Matcher;
                |
                |public class Sample {
                |    public boolean useRegex(String input) {
                |        // Compile regular expression
                |        Pattern p = Pattern.compile("%1${'$'}s"%2${'$'}s);
                |        // Match regex against input
                |        Matcher m = p.matcher(input);
                |        // Use results...
                |        return m.matches();
                |    }
                |}""".trimMargin()

        override fun generateOptionsCode(options: RecognizerCombiner.Options): String {
            val optionList = mutableListOf<String>()
            if (options.caseSensitive)
                optionList += "CASE_INSENSITIVE"
            if (options.dotMatchesLineBreaks)
                optionList += "DOTALL"
            if (options.multiline)
                optionList += "MULTILINE"

            optionList.ifEmpty { return "" }
            return optionList.joinToString(separator = " | ", prefix = " ,") { s -> "Pattern.$s" }
        }
    }

    /*class KotlinCodeGenerator() : CLikeCodeGenerator() {
        override val languageName: String get() = "Kotlin"
    }*/
}