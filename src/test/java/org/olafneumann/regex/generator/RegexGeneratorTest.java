package org.olafneumann.regex.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.jupiter.api.Test;

public class RegexGeneratorTest {
	@Test
	public void testRecognizeString1() throws Exception {
		final String input = "2020-03-12T13:34:56.123+1 WARN  [org.olafneumann.test.Test]: This is #simple. A line with a 'string' in the text";
		final RegexGenerator generator = new RegexGenerator(Configuration.get());

		final Collection<?> actual = generator.recognize(input);

		System.out.println("-------------------------------------");
		actual.forEach(System.out::println);

		assertThat(actual).size().isEqualTo(19);
	}

	@Test
	public void testRecognizeString2() throws Exception {
		final String input = "06:Feb:2011 \"This is an Example\"";
		final RegexGenerator generator = new RegexGenerator(Configuration.get());

		final Collection<?> actual = generator.recognize(input);

		System.out.println("-------------------------------------");
		actual.forEach(System.out::println);

		assertThat(actual).isNotEmpty();
	}
}
