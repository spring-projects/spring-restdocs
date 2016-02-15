/*
 * Copyright 2012-2016 the original author or authors.
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

package org.springframework.restdocs.snippet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import org.springframework.restdocs.operation.Operation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link TemplatedSnippet}.
 *
 * @author Andy Wilkinson
 */
public class TemplatedSnippetTests {

	@Test
	public void attributesAreCopied() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("a", "alpha");
		TemplatedSnippet snippet = new TestTemplatedSnippet(attributes);
		attributes.put("b", "bravo");
		assertThat(snippet.getAttributes().size(), is(1));
		assertThat(snippet.getAttributes(), hasEntry("a", (Object) "alpha"));
	}

	@Test
	public void nullAttributesAreTolerated() {
		assertThat(new TestTemplatedSnippet(null).getAttributes(), is(not(nullValue())));
		assertThat(new TestTemplatedSnippet(null).getAttributes().size(), is(0));
	}

	@Test
	public void snippetName() {
		assertThat(new TestTemplatedSnippet(Collections.<String, Object>emptyMap())
				.getSnippetName(), is(equalTo("test")));
	}

	private static class TestTemplatedSnippet extends TemplatedSnippet {

		protected TestTemplatedSnippet(Map<String, Object> attributes) {
			super("test", attributes);
		}

		@Override
		protected Map<String, Object> createModel(Operation operation) {
			return null;
		}

	}

}
