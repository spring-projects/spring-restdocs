/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.snippet;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;
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

		protected TestTemplatedSnippet(@Nullable Map<String, Object> attributes) {
			super("test", attributes);
		}

		@Override
		protected Map<String, Object> createModel(Operation operation) {
			return new HashMap<>();
		}

	}

}
