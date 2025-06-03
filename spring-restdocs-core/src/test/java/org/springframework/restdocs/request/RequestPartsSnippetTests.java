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

package org.springframework.restdocs.request;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTemplate;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link RequestPartsSnippet}.
 *
 * @author Andy Wilkinson
 */
class RequestPartsSnippetTests {

	@RenderedSnippetTest
	void requestParts(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one"), partWithName("b").description("two")))
			.document(operationBuilder.request("http://localhost")
				.part("a", "bravo".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(snippets.requestParts())
			.isTable((table) -> table.withHeader("Part", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void ignoredRequestPart(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestPartsSnippet(Arrays.asList(partWithName("a").ignored(), partWithName("b").description("two")))
			.document(operationBuilder.request("http://localhost")
				.part("a", "bravo".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(snippets.requestParts())
			.isTable((table) -> table.withHeader("Part", "Description").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void allUndocumentedRequestPartsCanBeIgnored(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestPartsSnippet(Arrays.asList(partWithName("b").description("two")), true)
			.document(operationBuilder.request("http://localhost")
				.part("a", "bravo".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(snippets.requestParts())
			.isTable((table) -> table.withHeader("Part", "Description").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void missingOptionalRequestPart(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one").optional(), partWithName("b").description("two")))
			.document(operationBuilder.request("http://localhost").part("b", "bravo".getBytes()).build());
		assertThat(snippets.requestParts())
			.isTable((table) -> table.withHeader("Part", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void presentOptionalRequestPart(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestPartsSnippet(Arrays.asList(partWithName("a").description("one").optional()))
			.document(operationBuilder.request("http://localhost").part("a", "one".getBytes()).build());
		assertThat(snippets.requestParts())
			.isTable((table) -> table.withHeader("Part", "Description").row("`a`", "one"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "request-parts", template = "request-parts-with-title")
	void requestPartsWithCustomAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one").attributes(key("foo").value("alpha")),
						partWithName("b").description("two").attributes(key("foo").value("bravo"))),
				attributes(key("title").value("The title")))
			.document(operationBuilder.request("http://localhost")
				.part("a", "alpha".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(snippets.requestParts()).contains("The title");
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "request-parts", template = "request-parts-with-extra-column")
	void requestPartsWithCustomDescriptorAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one").attributes(key("foo").value("alpha")),
						partWithName("b").description("two").attributes(key("foo").value("bravo"))))
			.document(operationBuilder.request("http://localhost")
				.part("a", "alpha".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(snippets.requestParts()).isTable((table) -> table.withHeader("Part", "Description", "Foo")
			.row("a", "one", "alpha")
			.row("b", "two", "bravo"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "request-parts", template = "request-parts-with-optional-column")
	void requestPartsWithOptionalColumn(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one").optional(), partWithName("b").description("two")))
			.document(operationBuilder.request("http://localhost")
				.part("a", "alpha".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(snippets.requestParts()).isTable((table) -> table.withHeader("Part", "Optional", "Description")
			.row("a", "true", "one")
			.row("b", "false", "two"));
	}

	@RenderedSnippetTest
	void additionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		RequestDocumentation.requestParts(partWithName("a").description("one"))
			.and(partWithName("b").description("two"))
			.document(operationBuilder.request("http://localhost")
				.part("a", "bravo".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(snippets.requestParts())
			.isTable((table) -> table.withHeader("Part", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void requestPartsWithEscapedContent(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		RequestDocumentation.requestParts(partWithName("Foo|Bar").description("one|two"))
			.document(operationBuilder.request("http://localhost").part("Foo|Bar", "baz".getBytes()).build());
		assertThat(snippets.requestParts())
			.isTable((table) -> table.withHeader("Part", "Description").row("`Foo|Bar`", "one|two"));
	}

	@SnippetTest
	void undocumentedPart(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestPartsSnippet(Collections.<RequestPartDescriptor>emptyList())
				.document(operationBuilder.request("http://localhost").part("a", "alpha".getBytes()).build()))
			.withMessage("Request parts with the following names were not documented: [a]");
	}

	@SnippetTest
	void missingPart(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestPartsSnippet(Arrays.asList(partWithName("a").description("one")))
				.document(operationBuilder.request("http://localhost").build()))
			.withMessage("Request parts with the following names were not found in the request: [a]");
	}

	@SnippetTest
	void undocumentedAndMissingParts(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestPartsSnippet(Arrays.asList(partWithName("a").description("one")))
				.document(operationBuilder.request("http://localhost").part("b", "bravo".getBytes()).build()))
			.withMessage("Request parts with the following names were not documented: [b]. Request parts with the"
					+ " following names were not found in the request: [a]");
	}

}
