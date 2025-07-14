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
 * Tests for {@link RequestHeadersSnippet}.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 */
class RequestHeadersSnippetTests {

	@RenderedSnippetTest
	void requestWithHeaders(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestHeadersSnippet(Arrays.asList(headerWithName("X-Test").description("one"),
				headerWithName("Accept").description("two"), headerWithName("Accept-Encoding").description("three"),
				headerWithName("Accept-Language").description("four"),
				headerWithName("Cache-Control").description("five"), headerWithName("Connection").description("six")))
			.document(operationBuilder.request("http://localhost")
				.header("X-Test", "test")
				.header("Accept", "*/*")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Accept-Language", "en-US,en;q=0.5")
				.header("Cache-Control", "max-age=0")
				.header("Connection", "keep-alive")
				.build());
		assertThat(snippets.requestHeaders()).isTable((table) -> table.withHeader("Name", "Description")
			.row("`X-Test`", "one")
			.row("`Accept`", "two")
			.row("`Accept-Encoding`", "three")
			.row("`Accept-Language`", "four")
			.row("`Cache-Control`", "five")
			.row("`Connection`", "six"));
	}

	@RenderedSnippetTest
	void caseInsensitiveRequestHeaders(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestHeadersSnippet(Arrays.asList(headerWithName("X-Test").description("one")))
			.document(operationBuilder.request("/").header("X-test", "test").build());
		assertThat(snippets.requestHeaders())
			.isTable((table) -> table.withHeader("Name", "Description").row("`X-Test`", "one"));
	}

	@RenderedSnippetTest
	void undocumentedRequestHeader(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestHeadersSnippet(Arrays.asList(headerWithName("X-Test").description("one"))).document(
				operationBuilder.request("http://localhost").header("X-Test", "test").header("Accept", "*/*").build());
		assertThat(snippets.requestHeaders())
			.isTable((table) -> table.withHeader("Name", "Description").row("`X-Test`", "one"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "request-headers", template = "request-headers-with-title")
	void requestHeadersWithCustomAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestHeadersSnippet(Arrays.asList(headerWithName("X-Test").description("one")),
				attributes(key("title").value("Custom title")))
			.document(operationBuilder.request("http://localhost").header("X-Test", "test").build());
		assertThat(snippets.requestHeaders()).contains("Custom title");
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "request-headers", template = "request-headers-with-extra-column")
	void requestHeadersWithCustomDescriptorAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one").attributes(key("foo").value("alpha")),
						headerWithName("Accept-Encoding").description("two").attributes(key("foo").value("bravo")),
						headerWithName("Accept").description("three").attributes(key("foo").value("charlie"))))
			.document(operationBuilder.request("http://localhost")
				.header("X-Test", "test")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Accept", "*/*")
				.build());
		assertThat(snippets.requestHeaders()).isTable((table) -> table.withHeader("Name", "Description", "Foo")
			.row("X-Test", "one", "alpha")
			.row("Accept-Encoding", "two", "bravo")
			.row("Accept", "three", "charlie"));
	}

	@RenderedSnippetTest
	void additionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		HeaderDocumentation
			.requestHeaders(headerWithName("X-Test").description("one"), headerWithName("Accept").description("two"),
					headerWithName("Accept-Encoding").description("three"),
					headerWithName("Accept-Language").description("four"))
			.and(headerWithName("Cache-Control").description("five"), headerWithName("Connection").description("six"))
			.document(operationBuilder.request("http://localhost")
				.header("X-Test", "test")
				.header("Accept", "*/*")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Accept-Language", "en-US,en;q=0.5")
				.header("Cache-Control", "max-age=0")
				.header("Connection", "keep-alive")
				.build());
		assertThat(snippets.requestHeaders()).isTable((table) -> table.withHeader("Name", "Description")
			.row("`X-Test`", "one")
			.row("`Accept`", "two")
			.row("`Accept-Encoding`", "three")
			.row("`Accept-Language`", "four")
			.row("`Cache-Control`", "five")
			.row("`Connection`", "six"));
	}

	@RenderedSnippetTest
	void tableCellContentIsEscapedWhenNecessary(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestHeadersSnippet(Arrays.asList(headerWithName("Foo|Bar").description("one|two")))
			.document(operationBuilder.request("http://localhost").header("Foo|Bar", "baz").build());
		assertThat(snippets.requestHeaders())
			.isTable((table) -> table.withHeader("Name", "Description").row("`Foo|Bar`", "one|two"));
	}

	@SnippetTest
	void missingRequestHeader(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestHeadersSnippet(Arrays.asList(headerWithName("Accept").description("one")))
				.document(operationBuilder.request("http://localhost").build()))
			.withMessage("Headers with the following names were not found in the request: [Accept]");
	}

	@SnippetTest
	void undocumentedRequestHeaderAndMissingRequestHeader(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestHeadersSnippet(Arrays.asList(headerWithName("Accept").description("one")))
				.document(operationBuilder.request("http://localhost").header("X-Test", "test").build()))
			.withMessageEndingWith("Headers with the following names were not found in the request: [Accept]");

	}

}
