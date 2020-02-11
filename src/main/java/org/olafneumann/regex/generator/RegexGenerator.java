package org.olafneumann.regex.generator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;

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
			proposals
				.add(
					new RecognizerMatch(
						matcher.start(configuration.getMainGroupName()),
						matcher.end(configuration.getMainGroupName()) - matcher.start(configuration.getMainGroupName()),
						matcher.group(configuration.getMainGroupName()),
						recognizer));
		}

		return proposals;
	}
}
