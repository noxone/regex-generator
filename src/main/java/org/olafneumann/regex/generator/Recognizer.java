package org.olafneumann.regex.generator;

import java.util.Optional;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Recognizer {
	private String name;
	private Optional<String> shortName;

	private String outputRegex;

	private Optional<String> description;

	private Optional<String> searchRegex;

	boolean active;

	@JsonCreator
	public Recognizer() {
	}

	Recognizer(final String name,
			final Optional<String> shortName,
			final boolean active,
			final String outputRegex,
			final Optional<String> searchRegex,
			final Optional<String> description) {
		this.name = name;
		this.shortName = shortName;
		this.outputRegex = outputRegex;
		this.description = description;
		this.searchRegex = searchRegex;
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public Optional<String> getShortName() {
		return shortName;
	}

	public String getDisplayName() {
		return getShortName().orElse(getName());
	}

	public String getOutputRegex() {
		return outputRegex;
	}

	public String getSearchRegex() {
		return searchRegex
			.map(regex -> String.format(regex, getOutputRegex()))
			.orElseGet(() -> String.format("(?<%s>%s)", Configuration.get().getMainGroupName(), getOutputRegex()));
	}

	public Optional<String> getDescription() {
		return description;
	}

	public Pattern getPattern() {
		return Patterns.getPattern(getSearchRegex());
	}

	public boolean isActive() {
		return active;
	}
}
