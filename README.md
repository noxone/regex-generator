# Regex Generator [![Build Status](https://github.com/noxone/regex-generator/actions/workflows/test.yml/badge.svg)](https://github.com/noxone/regex-generator/actions)

``Regex Generator`` is a tool to generate regular expressions. The goal is that people with less experience can create regex smoothly.

## Introduction

Regex Generator tries to help you create a first version of a regular expression to recognize certain texts. It is designed to help people create regular expressions by putting together well-known snippets.

Hopefully nobody will need to use ``substring()`` anymore.

## How to start

You may try [Regex Generator](https://noxone.github.io/regex-generator/) online.

And you can also start it via [docker](https://hub.docker.com/r/noxone/regexgenerator). Just use the following command and find Regex Generator on port 80 of your local machine:

```bash
docker run -d -p 80:80 noxone/regexgenerator
```

## How to build

1. Clone the project
2. Execute ``mvn clean package`` in the project's root directory
3. Find the output in directory ``./gh-pages/``

## Project goal

As written in the introduction the aim of the project is to enable everybody to use regular expressions instead of using ``substring()``. There are a lot of very nice tools to build, understand and even debug your regex. ``Regex Generator`` tries to add a little bit to these tools to give you at least a starting point how the regex you need might look like.

If you have ideas to improve the functionality or how things might be easier for the user, feel free to open an issue.

Hopefully this regex generator continues to grow to eventually support a really wide range of functions and regular expressions.

### Languages

The ``regex generator`` is currently able to generate code snippets in the following languages:

- C#
- Java
- JavaScript
- Kotlin
- PHP
- Ruby
- grep command line tool

If you're missing a language, just create a generator and open a pull-request or simply open a [new issue](https://github.com/noxone/regex-generator/issues/new?assignees=&labels=New+language&template=add-language.md&title=).

### Patterns

At the current stage there are ~20 patterns integrated in ``regex generator``. It is able to find simple patterns as well as very simple repetitions of smaller patterns.

In the final stage this Regex Generator shall support a wide range of use cases. Feel free to suggest new patterns via the [issues](https://github.com/noxone/regex-generator/issues/new?assignees=&labels=New+language&template=add-programming-language.md&title=).

## More ideas

More ideas for new features or how to improve the application are very welcome. Please add them to the [Github issues](https://github.com/noxone/regex-generator/issues).
