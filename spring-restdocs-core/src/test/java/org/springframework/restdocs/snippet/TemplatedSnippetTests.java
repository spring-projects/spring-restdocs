/*
 * Copyright 2014-2018 the original author or authors.
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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.test.ExpectedSnippets;
import org.springframework.restdocs.test.OperationBuilder;

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

	@Rule
	public OperationBuilder operationBuilder = new OperationBuilder(
			TemplateFormats.asciidoctor());

	@Rule
	public ExpectedSnippets snippets = new ExpectedSnippets(
			TemplateFormats.asciidoctor());

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

	@Test
	public void multipleSnippetsCanBeProducedFromTheSameTemplate() throws IOException {
		this.snippets.expect("multiple-snippets-one");
		this.snippets.expect("multiple-snippets-two");
		new TestTemplatedSnippet("one", "multiple-snippets")
				.document(this.operationBuilder.build());
		new TestTemplatedSnippet("two", "multiple-snippets")
				.document(this.operationBuilder.build());
	}

	private static class TestTemplatedSnippet extends TemplatedSnippet {

		protected TestTemplatedSnippet(String snippetName, String templateName) {
			super(templateName + "-" + snippetName, templateName,
					Collections.<String, Object>emptyMap());
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
