package org.olafneumann.regex.generator;

import java.util.Optional;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Recognizer {
	private String name;

	private String regex;

	private Optional<String> description;

	@JsonCreator
	public Recognizer() {}

	public String getName() {
		return name;
	}

	public String getRegex() {
		return regex;
	}

	public Optional<String> getDescription() {
		return description;
	}

	public Pattern getPattern() {
		return Patterns.getPattern(getRegex());
	}
}
