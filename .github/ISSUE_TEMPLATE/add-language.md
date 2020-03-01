---
name: Add language
about: Add a language to the generator
title: ''
labels: New language
assignees: ''

---

**How is your language called?**
e.g. Kotlin

**How does the snippet look like that we shall generate?**
The snippet should be compilable and runnable as is. The user should be able to just copy and paste the code and have a fully functional application.
````
fun useRegex(input: String): Boolean {
    val regex = Regex(pattern = "^2020-03-12T13:34:56\\.123Z INFO  \\[org\\.example\\.Class]: This is a #simple #logline containing a 'value'\\.$", options = setOf(RegexOption.IGNORE_CASE))
    return regex.matches(input)
}
````

**Anything special about string literals?**
Regex' contain a lot a strange characters that we might need to escaped when generating code. How does it work in your language?

**How can we specify options?**
Please describe how to apply the options the regex generator supports. If possible please specify *all* options because we don't necessarily know your language.

**Need a warning?**
Are there circumstances when you need to show a warning to the user? Maybe some option is not supported on all platforms? Let us know!
