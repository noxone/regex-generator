package org.olafneumann.regex.generator.regex

interface CodeGenerator {
    companion object {
        val list by lazy {
            listOf<CodeGenerator>(
                JavaCodeGenerator()
                ,KotlinCodeGenerator()
                //,PythonCodeGenerator()
            ).sortedBy { it.languageName }
        }
    }

    val languageName: String

    val highlightLanguage: String

    fun generateCode(pattern: String, options: RecognizerCombiner.Options): GeneratedSnippet
}

data class GeneratedSnippet(
    val snippet: String,
    val warnings: List<String> = emptyList()
)

internal abstract class SimpleReplacingCodeGenerator : CodeGenerator {
    protected abstract fun escapePattern(pattern: String): String

    protected abstract val templateCode: String

    protected abstract fun generateOptionsCode(options: RecognizerCombiner.Options): String

    protected open fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> = emptyList<String>()

    open override fun generateCode(pattern: String, options: RecognizerCombiner.Options): GeneratedSnippet =
        GeneratedSnippet(
            templateCode.replace("%1${'$'}s", escapePattern(pattern))
                .replace("%2${'$'}s", generateOptionsCode(options)),
            getWarnings(pattern, options)
        )

    protected open fun combineOptions(options: RecognizerCombiner.Options,
                                      valueForCaseInsensitive: String,
                                      valueForMultiline: String,
                                      valueForDotAll: String,
                                      valueIfNone: String? = null,
                                      prefix: String = "",
                                      separator: String = "",
                                      postfix: String = "",
                                      mapper: (option: String) -> String): String {
        val optionList = mutableListOf<String>()
        if (options.caseSensitive)
            optionList += valueForCaseInsensitive
        if (options.dotMatchesLineBreaks)
            optionList += valueForDotAll
        if (options.multiline)
            optionList += valueForMultiline

        optionList.ifEmpty { return valueIfNone ?: "" }
        return optionList.joinToString(separator = separator, prefix = prefix, postfix = postfix) { s -> mapper(s) }
    }
}

internal abstract class CLikeCodeGenerator() : SimpleReplacingCodeGenerator() {
    open override fun escapePattern(pattern: String): String =
        pattern.replace(Regex("([\\\\\"])"), "\\$1")
}

internal class JavaCodeGenerator() : CLikeCodeGenerator() {
    override val languageName: String get() = "Java"
    override val highlightLanguage: String get() = "java"

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

    override fun generateOptionsCode(options: RecognizerCombiner.Options): String
            = combineOptions(options, "CASE_INSENSITIVE", "MULTILINE", "DOTALL", prefix = " ,", separator = " | ") { s -> "Pattern.$s" }
}

internal class KotlinCodeGenerator() : CLikeCodeGenerator() {
    override val languageName: String get() = "Kotlin"
    override val highlightLanguage: String get() = "kotlin"

    override val templateCode: String get() = """fun useRegex(input: String): Boolean {
    val regex = Regex(pattern = "%1${'$'}s"%2${'$'}s)
    return regex.matches(input)
}"""

    override fun generateOptionsCode(options: RecognizerCombiner.Options): String
            = combineOptions(options, "IGNORE_CASE", "MULTILINE", "DOT_MATCHES_ALL", prefix = ", options = setOf(", postfix = ")", separator = ", ") { s -> "RegexOption.$s" }

    override fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> {
        if (options.dotMatchesLineBreaks)
            return listOf("The option 'RegexOption.DOT_MATCHES_ALL' is only supported on JVM runtime.")
        return emptyList()
    }
}

internal class PythonCodeGenerator() : SimpleReplacingCodeGenerator() {
    override val languageName: String
        get() = "Python"
    override val highlightLanguage: String
        get() = "python"

    override fun escapePattern(pattern: String): String =
        pattern.replace(Regex("([\\\\'])"), "\\$1")

    override val templateCode: String
        get() = """import re

pattern = '%1${'$'}s'
test_string = 'This is a test.'
result = re.match(pattern, test_string, flags = %2${'$'}s)

if result:
  print("Search successful.")
else:
  print("Search unsuccessful.")	"""

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(options, "I", "M", "S", valueIfNone = "0", separator = " | ") { s -> "re.$s"}
}