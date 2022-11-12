package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.regex.RegexCache
import org.olafneumann.regex.generator.regex.RegexMatchCombiner

internal class CSharpCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "C#",
    highlightLanguage = "csharp",
    templateCode = """using System;
using System.Text.RegularExpressions;

public class Sample
{
    public static bool useRegex(String input)
    {
        const Regex regex = new Regex("%1${'$'}s"%2${'$'}s);
        return regex.IsMatch(input);
    }
}"""
) {


    override fun transformPattern(pattern: String, options: RegexMatchCombiner.Options): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: RegexMatchCombiner.Options) =
        options.combine(
            valueForCaseInsensitive = "RegexOptions.IgnoreCase",
            valueForMultiline = "RegexOptions.Multiline",
            valueForDotAll = "RegexOptions.Singleline",
            separator = " | ",
            prefix = ", "
        )
}
