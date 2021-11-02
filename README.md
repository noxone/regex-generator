# Regex Generator [![Build Status](https://github.com/noxone/regex-generator/actions/workflows/test.yml/badge.svg?branch=master&event=push)](https://github.com/noxone/regex-generator/actions/workflows/test.yml)

``Regex Generator`` is a tool to generate simple regular expressions. The goal is that people with less experience can create regex smoothly.

Regex Generator tries to help you create a first version of a regular expression to recognize certain texts. It is designed to create regular expressions by putting together well-known snippets. This can be used as a starting point for regular expressions.

Hopefully nobody will need to use ``substring()`` anymore.

## Usage

### Online

Just go to [Regex Generator](https://regex-generator.olafneumann.org/) and try it online.

The page supports several search parameters to change the initial state of the options:

- ``sampleText`` changes the initial sample text of the regex generator
- ``flags`` changes the flags used to generate the language snippets. Any combination of the following characters are allowed:
  - ``i`` case insensitive
  - ``s`` dot matches line breaks
  - ``m`` multiline
- ``onlyPatterns`` this controls the "Generate only patterns" check box
- ``matchWholeLine`` this controls the "Match whole line" check box
- ``selection`` controls the matches that have been selected by the user

Example: [https://regex-generator.olafneumann.org/?sampleText=Some%20text...&flags=im&onlyPatterns=false&matchWholeLine=true&selection=5%7CMultiple%20characters](https://regex-generator.olafneumann.org/?sampleText=Some%20text...&flags=im&onlyPatterns=false&matchWholeLine=true&selection=5%7CMultiple%20characters)

### Docker

You can also start it via [docker](https://hub.docker.com/r/noxone/regexgenerator). Just use the following command and find Regex Generator on port 80 of your local machine:

```bash
docker run -d -p 80:80 noxone/regexgenerator
```

Of course the docker version supports the same search parameters as the actual website.

## Development

### Build

1. Clone the project
2. In the project's root directory execute

   ```bash
   gradlew clean build
   ```

3. Find the output in directory ``./build/distributions/``

### Live Development

For a nice development experience use

```bash
gradlew run --continuous
```

Using this command the page will reload automatically for every source file change.

### Detekt report

Detekt is a code style checker for Kotlin. It is integrated in the build process to keep the code clean.

You can find the Detekt report [here](https://regex-generator.olafneumann.org/reports/detekt.html).

## Project goal

As written in the introduction the aim of the project is to enable everybody to use regular expressions. The use of ``substring()`` is nearly always unnecessary and if code is written in a reasonable manner you won't need that method.

There are a lot of very nice tools to build, understand and even debug your regex. ``Regex Generator`` tries to add a little bit to these tools to give you at least a starting point how the regex you need might look like.

Hopefully this regex generator continues to grow to eventually support a really wide range of functions and regular expressions.

### Languages

The ``regex generator`` is currently able to generate code snippets in the following languages:

- C#
- grep (command line tool)
- Java
- JavaScript
- Kotlin
- PHP
- Ruby
- Swift

If you miss a language, just open a [new issue](https://github.com/noxone/regex-generator/issues/new?assignees=&labels=New+language&template=add-programming-language.md&title=) or if you like, program the corresponding generator and open a pull request.

### Patterns

At the current stage there are ~20 patterns integrated in ``regex generator``. It is able to find simple patterns as well as very simple repetitions of smaller patterns.

In the final stage this Regex Generator shall support a wide range of use cases. Feel free to suggest new patterns via the [issues](https://github.com/noxone/regex-generator/issues/new?assignees=&labels=&template=add-pattern.md&title=).

## More ideas

More ideas for new features or how to improve the application are very welcome. Please add them to the [Github issues](https://github.com/noxone/regex-generator/issues).
