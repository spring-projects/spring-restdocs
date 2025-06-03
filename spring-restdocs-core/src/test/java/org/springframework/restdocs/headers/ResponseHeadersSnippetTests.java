/*
 * Copyright 2014-2025 the original author or authors.
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

package org.springframework.restdocs.headers;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTemplate;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link ResponseHeadersSnippet}.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 */
class ResponseHeadersSnippetTests {

	@RenderedSnippetTest
	void responseWithHeaders(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseHeadersSnippet(Arrays.asList(headerWithName("X-Test").description("one"),
				headerWithName("Content-Type").description("two"), headerWithName("Etag").description("three"),
				headerWithName("Cache-Control").description("five"), headerWithName("Vary").description("six")))
			.document(operationBuilder.response()
				.header("X-Test", "test")
				.header("Content-Type", "application/json")
				.header("Etag", "lskjadldj3ii32l2ij23")
				.header("Cache-Control", "max-age=0")
				.header("Vary", "User-Agent")
				.build());
		assertThat(snippets.responseHeaders()).isTable((table) -> table.withHeader("Name", "Description")
			.row("`X-Test`", "one")
			.row("`Content-Type`", "two")
			.row("`Etag`", "three")
			.row("`Cache-Control`", "five")
			.row("`Vary`", "six"));
	}

	@RenderedSnippetTest
	void caseInsensitiveResponseHeaders(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseHeadersSnippet(Arrays.asList(headerWithName("X-Test").description("one")))
			.document(operationBuilder.response().header("X-test", "test").build());
		assertThat(snippets.responseHeaders())
			.isTable((table) -> table.withHeader("Name", "Description").row("`X-Test`", "one"));
	}

	@RenderedSnippetTest
	void undocumentedResponseHeader(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseHeadersSnippet(Arrays.asList(headerWithName("X-Test").description("one")))
			.document(operationBuilder.response().header("X-Test", "test").header("Content-Type", "*/*").build());
		assertThat(snippets.responseHeaders())
			.isTable((table) -> table.withHeader("Name", "Description").row("`X-Test`", "one"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "response-headers", template = "response-headers-with-title")
	void responseHeadersWithCustomAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseHeadersSnippet(Arrays.asList(headerWithName("X-Test").description("one")),
				attributes(key("title").value("Custom title")))
			.document(operationBuilder.response().header("X-Test", "test").build());
		assertThat(snippets.responseHeaders()).contains("Custom title");
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "response-headers", template = "response-headers-with-extra-column")
	void responseHeadersWithCustomDescriptorAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one").attributes(key("foo").value("alpha")),
						headerWithName("Content-Type").description("two").attributes(key("foo").value("bravo")),
						headerWithName("Etag").description("three").attributes(key("foo").value("charlie"))))
			.document(operationBuilder.response()
				.header("X-Test", "test")
				.header("Content-Type", "application/json")
				.header("Etag", "lskjadldj3ii32l2ij23")
				.build());
		assertThat(snippets.responseHeaders()).isTable((table) -> table.withHeader("Name", "Description", "Foo")
			.row("X-Test", "one", "alpha")
			.row("Content-Type", "two", "bravo")
			.row("Etag", "three", "charlie"));
	}

	@RenderedSnippetTest
	void additionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		HeaderDocumentation
			.responseHeaders(headerWithName("X-Test").description("one"),
					headerWithName("Content-Type").description("two"), headerWithName("Etag").description("three"))
			.and(headerWithName("Cache-Control").description("five"), headerWithName("Vary").description("six"))
			.document(operationBuilder.response()
				.header("X-Test", "test")
				.header("Content-Type", "application/json")
				.header("Etag", "lskjadldj3ii32l2ij23")
				.header("Cache-Control", "max-age=0")
				.header("Vary", "User-Agent")
				.build());
		assertThat(snippets.responseHeaders()).isTable((table) -> table.withHeader("Name", "Description")
			.row("`X-Test`", "one")
			.row("`Content-Type`", "two")
			.row("`Etag`", "three")
			.row("`Cache-Control`", "five")
			.row("`Vary`", "six"));
	}

	@RenderedSnippetTest
	void tableCellContentIsEscapedWhenNecessary(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseHeadersSnippet(Arrays.asList(headerWithName("Foo|Bar").description("one|two")))
			.document(operationBuilder.response().header("Foo|Bar", "baz").build());
		assertThat(snippets.responseHeaders())
			.isTable((table) -> table.withHeader("Name", "Description").row("`Foo|Bar`", "one|two"));
	}

	@SnippetTest
	void missingResponseHeader(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(
					() -> new ResponseHeadersSnippet(Arrays.asList(headerWithName("Content-Type").description("one")))
						.document(operationBuilder.response().build()))
			.withMessage("Headers with the following names were not found" + " in the response: [Content-Type]");
	}

	@SnippetTest
	void undocumentedResponseHeaderAndMissingResponseHeader(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(
					() -> new ResponseHeadersSnippet(Arrays.asList(headerWithName("Content-Type").description("one")))
						.document(operationBuilder.response().header("X-Test", "test").build()))
			.withMessageEndingWith("Headers with the following names were not found in the response: [Content-Type]");
	}

}
