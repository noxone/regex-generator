package org.olafneumann.regex.generator;

import java.util.Optional;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Recognizer {
	private String name;

	private String outputRegex;

	private Optional<String> description;

	private final Optional<String> searchRegex = Optional.empty();

	@JsonCreator
	public Recognizer() {
	}

	public String getName() {
		return name;
	}

	public String getOutputRegex() {
		return outputRegex;
	}

	public String getSearchRegex() {
		return searchRegex.orElse(getOutputRegex());
	}

	public Optional<String> getDescription() {
		return description;
	}

	public Pattern getPattern() {
		return Patterns.getPattern(getSearchRegex());
	}
}
