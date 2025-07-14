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

package org.springframework.restdocs.snippet;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TemplatedSnippet}.
 *
 * @author Andy Wilkinson
 */
class TemplatedSnippetTests {

	@Test
	void attributesAreCopied() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("a", "alpha");
		TemplatedSnippet snippet = new TestTemplatedSnippet(attributes);
		attributes.put("b", "bravo");
		assertThat(snippet.getAttributes()).hasSize(1);
		assertThat(snippet.getAttributes()).containsEntry("a", "alpha");
	}

	@Test
	void nullAttributesAreTolerated() {
		assertThat(new TestTemplatedSnippet(null).getAttributes()).isNotNull();
		assertThat(new TestTemplatedSnippet(null).getAttributes()).isEmpty();
	}

	@Test
	void snippetName() {
		assertThat(new TestTemplatedSnippet(Collections.<String, Object>emptyMap()).getSnippetName()).isEqualTo("test");
	}

	@RenderedSnippetTest
	void multipleSnippetsCanBeProducedFromTheSameTemplate(OperationBuilder operationBuilder, AssertableSnippets snippet)
			throws IOException {
		new TestTemplatedSnippet("one", "multiple-snippets").document(operationBuilder.build());
		new TestTemplatedSnippet("two", "multiple-snippets").document(operationBuilder.build());
		assertThat(snippet.named("multiple-snippets-one")).exists();
		assertThat(snippet.named("multiple-snippets-two")).exists();
	}

	private static class TestTemplatedSnippet extends TemplatedSnippet {

		protected TestTemplatedSnippet(String snippetName, String templateName) {
			super(templateName + "-" + snippetName, templateName, Collections.<String, Object>emptyMap());
		}

		protected TestTemplatedSnippet(Map<String, Object> attributes) {
			super("test", attributes);
		}

		@Override
		protected Map<String, Object> createModel(Operation operation) {
			return new HashMap<>();
		}

	}

}
