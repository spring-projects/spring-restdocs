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

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Tests for {@link RequestPartFieldsSnippet}.
 *
 * @author Mathieu Pousse
 * @author Andy Wilkinson
 */
public class RequestPartFieldsSnippetTests {

	@RenderedSnippetTest
	void mapRequestPartFields(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestPartFieldsSnippet("one",
				Arrays.asList(fieldWithPath("a.b").description("one"), fieldWithPath("a.c").description("two"),
						fieldWithPath("a").description("three")))
			.document(operationBuilder.request("http://localhost")
				.part("one", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
				.build());
		assertThat(snippets.requestPartFields("one")).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a.b`", "`Number`", "one")
			.row("`a.c`", "`String`", "two")
			.row("`a`", "`Object`", "three"));
	}

	@RenderedSnippetTest
	void mapRequestPartSubsectionFields(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestPartFieldsSnippet("one", beneathPath("a"),
				Arrays.asList(fieldWithPath("b").description("one"), fieldWithPath("c").description("two")))
			.document(operationBuilder.request("http://localhost")
				.part("one", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
				.build());
		assertThat(snippets.requestPartFields("one", "beneath-a"))
			.isTable((table) -> table.withHeader("Path", "Type", "Description")
				.row("`b`", "`Number`", "one")
				.row("`c`", "`String`", "two"));
	}

	@RenderedSnippetTest
	void multipleRequestParts(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		Operation operation = operationBuilder.request("http://localhost")
			.part("one", "{}".getBytes())
			.and()
			.part("two", "{}".getBytes())
			.build();
		new RequestPartFieldsSnippet("one", Collections.<FieldDescriptor>emptyList()).document(operation);
		new RequestPartFieldsSnippet("two", Collections.<FieldDescriptor>emptyList()).document(operation);
		assertThat(snippets.requestPartFields("one")).isNotNull();
		assertThat(snippets.requestPartFields("two")).isNotNull();
	}

	@RenderedSnippetTest
	void allUndocumentedRequestPartFieldsCanBeIgnored(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestPartFieldsSnippet("one", Arrays.asList(fieldWithPath("b").description("Field b")), true).document(
				operationBuilder.request("http://localhost").part("one", "{\"a\": 5, \"b\": 4}".getBytes()).build());
		assertThat(snippets.requestPartFields("one"))
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`b`", "`Number`", "Field b"));
	}

	@RenderedSnippetTest
	void additionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		PayloadDocumentation
			.requestPartFields("one", fieldWithPath("a.b").description("one"), fieldWithPath("a.c").description("two"))
			.and(fieldWithPath("a").description("three"))
			.document(operationBuilder.request("http://localhost")
				.part("one", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
				.build());
		assertThat(snippets.requestPartFields("one")).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a.b`", "`Number`", "one")
			.row("`a.c`", "`String`", "two")
			.row("`a`", "`Object`", "three"));
	}

	@RenderedSnippetTest
	void prefixedAdditionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		PayloadDocumentation.requestPartFields("one", fieldWithPath("a").description("one"))
			.andWithPrefix("a.", fieldWithPath("b").description("two"), fieldWithPath("c").description("three"))
			.document(operationBuilder.request("http://localhost")
				.part("one", "{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes())
				.build());
		assertThat(snippets.requestPartFields("one")).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a`", "`Object`", "one")
			.row("`a.b`", "`Number`", "two")
			.row("`a.c`", "`String`", "three"));
	}

	@SnippetTest
	void undocumentedRequestPartField(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestPartFieldsSnippet("part", Collections.<FieldDescriptor>emptyList())
				.document(operationBuilder.request("http://localhost").part("part", "{\"a\": 5}".getBytes()).build()))
			.withMessageStartingWith("The following parts of the payload were not documented:");
	}

	@SnippetTest
	void missingRequestPartField(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestPartFieldsSnippet("part", Arrays.asList(fieldWithPath("b").description("one")))
				.document(operationBuilder.request("http://localhost").part("part", "{\"a\": 5}".getBytes()).build()))
			.withMessageStartingWith("The following parts of the payload were not documented:");
	}

	@SnippetTest
	void missingRequestPart(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class).isThrownBy(
				() -> new RequestPartFieldsSnippet("another", Arrays.asList(fieldWithPath("a.b").description("one")))
					.document(operationBuilder.request("http://localhost")
						.part("part", "{\"a\": {\"b\": 5}}".getBytes())
						.build()))
			.withMessage("A request part named 'another' was not found in the request");
	}

}
