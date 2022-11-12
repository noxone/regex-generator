package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.regex.RegexCache
import org.olafneumann.regex.generator.regex.Options

internal class CRegexCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "C (regex.h)",
    highlightLanguage = "c",
    templateCode = """#include <regex.h>

int useRegex(char* textToCheck) {
    regex_t compiledRegex;
    int reti;
    int actualReturnValue = -1;
    char messageBuffer[100];

    /* Compile regular expression */
    reti = regcomp(&compiledRegex, "%1${'$'}s", REG_EXTENDED%2${'$'}s);
    if (reti) {
        fprintf(stderr, "Could not compile regex\n");
        return -2;
    }

    /* Execute compiled regular expression */
    reti = regexec(&compiledRegex, textToCheck, 0, NULL, 0);
    if (!reti) {
        puts("Match");
        actualReturnValue = 0;
    } else if (reti == REG_NOMATCH) {
        puts("No match");
        actualReturnValue = 1;
    } else {
        regerror(reti, &compiledRegex, messageBuffer, sizeof(messageBuffer));
        fprintf(stderr, "Regex match failed: %s\n", messageBuffer);
        actualReturnValue = -3;
    }

    /* Free memory allocated to the pattern buffer by regcomp() */
    regfree(&compiledRegex);
    return actualReturnValue;
}"""
) {
    override fun transformPattern(pattern: String, options: Options): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: Options) =
        options.combine(
            valueForCaseInsensitive = "REG_ICASE",
            valueForMultiline = "REG_NEWLINE",
            separator = " | ",
            prefix = " | "
        )

    override fun getWarnings(pattern: String, options: Options): List<String> {
        if (options.dotMatchesLineBreaks)
            return listOf("DOT_ALL is not supported by regex.h")

        return emptyList()
    }
}
