package org.olafneumann.regex.generator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;

import com.google.common.base.Strings;

public class RegexGenerator {
	private final Configuration configuration;

	public RegexGenerator(final Configuration configuration) {
		this.configuration = configuration;
	}

	public Collection<?> recognize(final String input) {
		return recognize(configuration.getRecognizers(), input);
	}

	private Collection<Object> recognize(final Collection<Recognizer> recognizers, final String input) {
		final Map<Recognizer, List<RecognizerMatch>> map = recognizers
			.stream()
			.collect(toMap(Function.identity(), r -> findPatternProposals(r, input)));

		return map.values().stream().flatMap(Collection::stream).sorted(RecognizerMatch.COMPARATOR).collect(toList());
	}

	private List<RecognizerMatch> findPatternProposals(final Recognizer recognizer, final String input) {
		final Matcher matcher = recognizer.getPattern().matcher(input);

		final List<RecognizerMatch> proposals = new ArrayList<>();
		while (matcher.find()) {
			proposals.add(new RecognizerMatch(matcher.start(), matcher.end() - matcher.start(), getMatch(matcher), recognizer));
		}

		return proposals;
	}

	private String getMatch(final Matcher matcher) {
		try {
			return Optional.ofNullable(Strings.emptyToNull(matcher.group("main"))).orElse(matcher.group());
		} catch (final IllegalArgumentException e) {
			return matcher.group();
		}
	}
}
