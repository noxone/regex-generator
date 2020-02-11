package org.olafneumann.regex.generator;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

final class Patterns {
	private static final LoadingCache<String, Pattern> CACHE = CacheBuilder.newBuilder()
			.build(new CacheLoader<String, Pattern>() {
				@Override
				public Pattern load(final String regex) {
					return Pattern.compile(regex);
				}
			});

	static Pattern getPattern(final String regex) {
		try {
			return CACHE.get(regex);
		} catch (final ExecutionException e) {
			throw new RuntimeException(String.format("Unable to create pattern for: %s", regex), e);
		}
	}

	private Patterns() {
		// nothing to do
	}
}
