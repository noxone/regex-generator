package org.olafneumann.regex.generator;

import java.util.Comparator;

public class RecognizerMatch {
	private static final Comparator<RecognizerMatch> COMPARATOR_BY_START = //
			(a, b) -> Integer.compare(a.getStart(), b.getStart());
	private static final Comparator<RecognizerMatch> COMPARATOR_BY_LENGTH = //
			(a, b) -> -Integer.compare(a.getLength(), b.getLength());
	private static final Comparator<RecognizerMatch> COMPARATOR_BY_NAME = //
			(a, b) -> a.getRecognizer().getName().compareToIgnoreCase(b.getRecognizer().getName());
	static final Comparator<RecognizerMatch> COMPARATOR = COMPARATOR_BY_START//
			.thenComparing(COMPARATOR_BY_LENGTH)//
			.thenComparing(COMPARATOR_BY_NAME);

	private final int start;
	private final int length;
	private final String inputPart;
	private final Recognizer recognizer;

	RecognizerMatch(final int start, final int length, final String inputPart, final Recognizer recognizer) {
		this.start = start;
		this.length = length;
		this.inputPart = inputPart;
		this.recognizer = recognizer;
	}

	public int getLength() {
		return length;
	}

	public Recognizer getRecognizer() {
		return recognizer;
	}

	public String getInputPart() {
		return inputPart;
	}

	public int getStart() {
		return start;
	}

	@Override
	public String toString() {
		return String
			.format("[%s/%s] (%s: %s) %s", start, start + length, recognizer.getName(), recognizer.getOutputRegex(), inputPart);
	}
}
