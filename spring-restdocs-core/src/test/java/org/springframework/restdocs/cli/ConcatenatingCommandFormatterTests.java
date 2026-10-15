/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.cli;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CommandFormatter}.
 *
 * @author Tomasz Kopczynski
 * @author Andy Wilkinson
 */
class ConcatenatingCommandFormatterTests {

	private CommandFormatter singleLineFormat = new ConcatenatingCommandFormatter(" ");

	@Test
	void formattingAnEmptyListProducesAnEmptyString() {
		assertThat(this.singleLineFormat.format(Collections.<String>emptyList())).isEqualTo("");
	}

	@Test
	@SuppressWarnings("NullAway") // Test null handling
	void formattingNullProducesAnEmptyString() {
		assertThat(this.singleLineFormat.format(null)).isEqualTo("");
	}

	@Test
	void formattingASingleElement() {
		assertThat(this.singleLineFormat.format(Collections.singletonList("alpha"))).isEqualTo(" alpha");
	}

	@Test
	void formattingMultipleElements() {
		assertThat(this.singleLineFormat.format(Arrays.asList("alpha", "bravo"))).isEqualTo(" alpha bravo");
	}

}
