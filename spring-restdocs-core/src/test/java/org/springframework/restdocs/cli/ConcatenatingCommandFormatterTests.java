/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link CommandFormatter}.
 *
 * @author Tomasz Kopczynski
 */
public class ConcatenatingCommandFormatterTests {

	private CommandFormatter singleLineFormat = CliDocumentation.singleLineFormat();

	private static final String STR = "test";

	@Test
	public void noElementsTest() {
		assertThat(this.singleLineFormat.format(Collections.<String>emptyList()), is(equalTo("")));
		assertThat(this.singleLineFormat.format(null), is(equalTo("")));

	}

	@Test
	public void singleElementTest() {
		assertThat(this.singleLineFormat.format(Collections.singletonList(STR)), is(equalTo(String.format(" %s", STR))));
	}

	@Test
	public void twoElementsTest() {
		assertThat(this.singleLineFormat.format(Arrays.asList(STR, STR)), is(equalTo(String.format(" %s %s", STR, STR))));
	}
}
