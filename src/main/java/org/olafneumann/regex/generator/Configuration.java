package org.olafneumann.regex.generator;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Configuration {
	private static final String SETTINGS_FILE_NAME = "settings.yaml";

	private static final Configuration INSTANCE = readConfiguration();

	private static ObjectMapper createObjectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		objectMapper.registerModule(new Jdk8Module());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		return objectMapper;
	}

	private static Configuration readConfiguration() {
		try {
			final URL configUrl = Resources.getResource(SETTINGS_FILE_NAME);
			final String configString = Resources.toString(configUrl, Charsets.UTF_8);
			return check(createObjectMapper().readValue(configString, Configuration.class));
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to read configuration", e);
		}
	}

	private static Configuration check(final Configuration configuration) {
		configuration.getRecognizers().stream().map(Recognizer::getName).collect(toList());
		return configuration;
	}

	public static Configuration get() {
		return INSTANCE;
	}

	@JsonProperty
	private List<Recognizer> recognizers = new ArrayList<>();

	private Map<String, String> groups;

	@JsonCreator
	private Configuration() {
		// fields will be filled by jackson
	}

	public List<Recognizer> getRecognizers() {
		return recognizers;
	}

	@SuppressWarnings("unused")
	private void setRecognizers(final List<Recognizer> recognizers) {
		this.recognizers = recognizers;
	}

	public String getMainGroupName() {
		return Objects.requireNonNull(groups.get("main"));
	}
}
