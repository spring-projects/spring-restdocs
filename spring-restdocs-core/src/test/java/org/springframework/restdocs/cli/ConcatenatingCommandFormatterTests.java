/*
 * Copyright 2014-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
