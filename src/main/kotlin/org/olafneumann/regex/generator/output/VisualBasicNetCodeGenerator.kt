package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.regex.RegexCache
import org.olafneumann.regex.generator.regex.RegexCombiner

internal class VisualBasicNetCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Visual Basic .NET",
    highlightLanguage = "vbnet",
    templateCode = """Imports System.Text.RegularExpressions

Public Module Sample
    Public Function useRegex(ByVal input As String) As Boolean
        Dim regex = New Regex("%1${'$'}s"%2${'$'}s)
        Return regex.IsMatch(input)
    End Function
End Module"""
) {


    override fun transformPattern(pattern: String, options: RegexCombiner.Options): String =
        pattern.replace(RegexCache.get("\""), "\"\"")

    override fun generateOptionsCode(options: RegexCombiner.Options) =
        options.combine(
            valueForCaseInsensitive = "RegexOptions.IgnoreCase",
            valueForMultiline = "RegexOptions.Multiline",
            valueForDotAll = "RegexOptions.Singleline",
            separator = " Or ",
            prefix = ", "
        )
}
