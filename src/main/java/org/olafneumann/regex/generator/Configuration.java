package org.olafneumann.regex.generator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Configuration {
	private static final String SETTINGS_FILE_NAME = "settings.json";

	private static final Configuration INSTANCE = readConfiguration();

	private static ObjectMapper createObjectMapper() {
//		final SimpleModule module = new SimpleModule("Oversigt-API");
//		module.addSerializer(Duration.class, serializer(Duration.class, Duration::toString));
//		module.addDeserializer(Duration.class, deserializer(Duration.class, Duration::parse));

		final ObjectMapper objectMapper = new ObjectMapper();
		// objectMapper.registerModule(module);
		// objectMapper.registerModule(new Jdk8Module());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		return objectMapper;
	}

	private static Configuration readConfiguration() {
		try {
			final URL configUrl = Resources.getResource(SETTINGS_FILE_NAME);
			final String configString = Resources.toString(configUrl, Charsets.UTF_8);
			return createObjectMapper().readValue(configString, Configuration.class);
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to read configuration", e);
		}
	}

	public static Configuration get() {
		return INSTANCE;
	}

	@JsonProperty
	private List<Recognizer> recognizers = new ArrayList<>();

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
}
