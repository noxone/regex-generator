package org.olafneumann.regex.generator;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class Recognizer {
	private static final LoadingCache<String, Pattern> CACHE = CacheBuilder.newBuilder()
			.build(new CacheLoader<String, Pattern>() {
				@Override
				public Pattern load(final String regex) {
					return Pattern.compile(regex);
				}
			});

	private final String name;
	private final String regex;
	private final String description;

	@JsonCreator
	public Recognizer(final @JsonProperty("name") String name, final @JsonProperty("regex") String regex,
			final @JsonProperty("description") String description) {
		this.name = name;
		this.regex = regex;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getRegex() {
		return regex;
	}

	public String getDescription() {
		return description;
	}

	public Pattern getPattern() {
		try {
			return CACHE.get(getRegex());
		} catch (final ExecutionException e) {
			throw new RuntimeException(String.format("Unable to create pattern for: %s", getRegex()), e);
		}
	}
}
