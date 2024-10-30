# Regex Generator [![Build and publish project](https://github.com/noxone/regex-generator/actions/workflows/publish-project.yml/badge.svg)](https://github.com/noxone/regex-generator/actions/workflows/publish-project.yml)

``Regex Generator`` is a tool to generate simple regular expressions. The goal is that people with less experience can create regex smoothly.

``Regex Generator`` tries to help you create a first version of a regular expression to recognize certain texts. It is designed to create regular expressions by putting together well-known snippets. This can be used as a starting point for regular expressions.

Hopefully nobody will need to use ``substring()`` anymore.

## Usage

### Online

Go to [Regex Generator](https://regex-generator.olafneumann.org/) and try it online.

The page supports several search parameters to change the initial state of the options:

- ``sampleText`` changes the initial sample text of the regex generator
- ``flags`` changes the flags used to generate the language snippets. Any combination of the following characters are allowed:
  - ``i`` case insensitive
  - ``s`` dot matches line breaks
  - ``m`` multiline
  - ``P`` this controls the "Generate Only Patterns" check box
  - ``W`` this controls the "Match Whole Line" check box
  - ``L`` this controls the "Generate Lower Case" check box
- ``selection`` controls the matches that have been selected by the user
<!-- - ``capGroups`` controls the capturing groups that are created by the user -->

Example: [https://regex-generator.olafneumann.org/?sampleText=Some%20text...&flags=imL&selection=5%7CMultiple%20characters](https://regex-generator.olafneumann.org/?sampleText=Some%20text...&flags=imL&selection=5%7CMultiple%20characters)

### Docker

You can also start it via Docker. Please find the generated images in this [repository](https://hub.docker.com/r/noxone/regexgenerator). Just use the following command and use ``Regex Generator`` via port 80 of your local machine:

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

   Or use docker to build the project:

   ```bash
   docker build . -t noxone/regexgenerator
   ```

3. Find the output in directory ``./build/distributions/``

### Live Development

For a nice development experience use

```bash
gradlew run --continuous
```

Using this command the page will reload automatically for every source file change.

### Reports

#### Unit Tests

Well, we don't have enough unit tests. But there are a few. See the [results](https://regex-generator.olafneumann.org/reports/allTests).

#### Detekt

[Detekt](https://github.com/detekt/detekt) is a code style checker for Kotlin. It is integrated in the build process to keep the code clean.

You can find the Detekt report for this project [here](https://regex-generator.olafneumann.org/reports/detekt.html).

## Project goal

As written in the introduction the aim of the project is to enable everybody to use regular expressions. The use of ``substring()`` is nearly always unnecessary and if code is written in a reasonable manner you barely need that method.

There are a lot of very nice tools to build, understand and even debug your regex. ``Regex Generator`` tries to add a little bit to these tools to give you at least a starting point how the regex you need might look like.

Hopefully this regex generator continues to grow to eventually support a really wide range of functions and regular expressions.

### Languages

``Regex Generator`` is currently able to generate code snippets in the following languages:

- C (using `regex.h`)
- C#
- Go
- grep (command line tool)
- Java
- JavaScript
- Kotlin
- PHP
- Python
- Ruby
- Swift
- Visual Basic .NET

If you miss a language, just open a [new issue](https://github.com/noxone/regex-generator/issues/new?assignees=&labels=New+language&template=add-programming-language.md&title=) or if you like, program the corresponding generator and open a pull request.

### Patterns

Currently, about 30 patterns of different complexity are integrated in ``Regex Generator``. It is able to recognize simple repetitions and combinations of multiple patterns.

To know more about the patterns, it's probably best to have a look into the [code](https://github.com/noxone/regex-generator/blob/main/src/jsMain/kotlin/org/olafneumann/regex/generator/recognizer/RecognizerRegistry.kt).

The list of included patterns is certainly not exhaustive. Nevertheless, it should cover a good number of use cases for beginners to start their regex with. Suggestions for additional patterns are very welcome and will be gladly included in the list. This is easily done via the [issues](https://github.com/noxone/regex-generator/issues/new?assignees=&labels=&template=add-pattern.md&title=).

## Starting points for how the code works:
- [P2MatchPresenter.kt#L85](https://github.com/noxone/regex-generator/blob/main/src/jsMain/kotlin/org/olafneumann/regex/generator/ui/parts/P2MatchPresenter.kt#L85): This is where HTML and DOM manipulation happens. Most of that is hidden behind some Kotlin libraries or some helper functions so you barely see any direct DOM interaction. But you probably understand what's going on: Create new elements and replace the old ones...
- [RecognizerRegistry.kt#L101](https://github.com/noxone/regex-generator/blob/main/src/jsMain/kotlin/org/olafneumann/regex/generator/recognizer/RecognizerRegistry.kt#L101): The actual recognizing of possible matches is done here. The output is simply a list of all regex matches that have been found.
- The whole UI state (as well as the match selection stuff) is done in models, so it doesn't mix with the DOM or the regex code. The Regex stuff is quite complex, so there is a separate model for that, too.
  - [PatternRecognizerModel.kt](https://github.com/noxone/regex-generator/blob/main/src/jsMain/kotlin/org/olafneumann/regex/generator/model/PatternRecognizerModel.kt): Takes the recognized matches and manages them together with user selection. This model also has some logic to keep selection even if the input changes.  
  Please note: At this point we will still only have a simple list of possible matches. Some of them are marked as "selected".
  - [DisplayModel.kt#L28](https://github.com/noxone/regex-generator/blob/main/src/jsMain/kotlin/org/olafneumann/regex/generator/ui/model/DisplayModel.kt#L28) takes these recognized (and possibly selected) matches and computes so called `MatchPresenter`s. These contain the information, how matches can be grouped (for the UI) and which match can be selected at which position. So, these MatchPresenters are the a first connection to what is displayed for the user - and is based on the simple list of the previous model.  
  The [DisplayModel](https://github.com/noxone/regex-generator/blob/main/src/jsMain/kotlin/org/olafneumann/regex/generator/ui/model/DisplayModel.kt#L133) also contains the logic, how to distribute the MatchPresenters to each row .

## More ideas

More ideas for new features or how to improve the application are very welcome. Please add them to the [Github issues](https://github.com/noxone/regex-generator/issues).
