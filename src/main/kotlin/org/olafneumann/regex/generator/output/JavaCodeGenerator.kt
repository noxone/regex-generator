package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.regex.Options
import org.olafneumann.regex.generator.regex.RegexCache

class JavaCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Java",
    highlightLanguage = "java",
    templateCode = """import java.util.regex.Pattern;
                |import java.util.regex.Matcher;
                |
                |public class Sample {
                |    public static boolean useRegex(final String input) {
                |        // Compile regular expression
                |        final Pattern pattern = Pattern.compile("%1${'$'}s"%2${'$'}s);
                |        // Match regex against input
                |        final Matcher matcher = pattern.matcher(input);
                |        // Use results...
                |        return matcher.matches();
                |    }
                |}""".trimMargin()
) {

    override fun transformPattern(pattern: String, options: Options): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: Options): String = options.combine(
        valueForCaseInsensitive = "Pattern.CASE_INSENSITIVE",
        valueForMultiline = "Pattern.MULTILINE",
        valueForDotAll = "Pattern.DOTALL",
        prefix = ", ",
        separator = " | "
    )
}
