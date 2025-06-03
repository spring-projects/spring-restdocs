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

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTemplate;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link RequestFieldsSnippet}.
 *
 * @author Andy Wilkinson
 * @author Sungjun Lee
 */
class RequestFieldsSnippetTests {

	@RenderedSnippetTest
	void mapRequestWithFields(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one"),
				fieldWithPath("a.c").description("two"), fieldWithPath("a").description("three")))
			.document(operationBuilder.request("http://localhost")
				.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
				.build());
		assertThat(snippets.requestFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a.b`", "`Number`", "one")
			.row("`a.c`", "`String`", "two")
			.row("`a`", "`Object`", "three"));
	}

	@RenderedSnippetTest
	void mapRequestWithNullField(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")))
			.document(operationBuilder.request("http://localhost").content("{\"a\": {\"b\": null}}").build());
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a.b`", "`Null`", "one"));
	}

	@RenderedSnippetTest
	void entireSubsectionsCanBeDocumented(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(Arrays.asList(subsectionWithPath("a").description("one")))
			.document(operationBuilder.request("http://localhost")
				.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
				.build());
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a`", "`Object`", "one"));
	}

	@RenderedSnippetTest
	void subsectionOfMapRequest(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		requestFields(beneathPath("a"), fieldWithPath("b").description("one"), fieldWithPath("c").description("two"))
			.document(operationBuilder.request("http://localhost")
				.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
				.build());
		assertThat(snippets.requestFields("beneath-a"))
			.isTable((table) -> table.withHeader("Path", "Type", "Description")
				.row("`b`", "`Number`", "one")
				.row("`c`", "`String`", "two"));
	}

	@RenderedSnippetTest
	void subsectionOfMapRequestWithCommonPrefix(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		requestFields(beneathPath("a")).andWithPrefix("b.", fieldWithPath("c").description("two"))
			.document(operationBuilder.request("http://localhost")
				.content("{\"a\": {\"b\": {\"c\": \"charlie\"}}}")
				.build());
		assertThat(snippets.requestFields("beneath-a"))
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`b.c`", "`String`", "two"));
	}

	@RenderedSnippetTest
	void arrayRequestWithFields(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("[]").description("one"), fieldWithPath("[]a.b").description("two"),
						fieldWithPath("[]a.c").description("three"), fieldWithPath("[]a").description("four")))
			.document(operationBuilder.request("http://localhost")
				.content("[{\"a\": {\"b\": 5, \"c\":\"charlie\"}}," + "{\"a\": {\"b\": 4, \"c\":\"chalk\"}}]")
				.build());
		assertThat(snippets.requestFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`[]`", "`Array`", "one")
			.row("`[]a.b`", "`Number`", "two")
			.row("`[]a.c`", "`String`", "three")
			.row("`[]a`", "`Object`", "four"));
	}

	@RenderedSnippetTest
	void arrayRequestWithAlwaysNullField(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("[]a.b").description("one")))
			.document(operationBuilder.request("http://localhost")
				.content("[{\"a\": {\"b\": null}}," + "{\"a\": {\"b\": null}}]")
				.build());
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`[]a.b`", "`Null`", "one"));
	}

	@RenderedSnippetTest
	void subsectionOfArrayRequest(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		requestFields(beneathPath("[].a"), fieldWithPath("b").description("one"), fieldWithPath("c").description("two"))
			.document(operationBuilder.request("http://localhost")
				.content("[{\"a\": {\"b\": 5, \"c\": \"charlie\"}}]")
				.build());
		assertThat(snippets.requestFields("beneath-[].a"))
			.isTable((table) -> table.withHeader("Path", "Type", "Description")
				.row("`b`", "`Number`", "one")
				.row("`c`", "`String`", "two"));
	}

	@RenderedSnippetTest
	void ignoredRequestField(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").ignored(), fieldWithPath("b").description("Field b")))
			.document(operationBuilder.request("http://localhost").content("{\"a\": 5, \"b\": 4}").build());
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`b`", "`Number`", "Field b"));
	}

	@RenderedSnippetTest
	void entireSubsectionCanBeIgnored(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(
				Arrays.asList(subsectionWithPath("a").ignored(), fieldWithPath("c").description("Field c")))
			.document(operationBuilder.request("http://localhost").content("{\"a\": {\"b\": 5}, \"c\": 4}").build());
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`c`", "`Number`", "Field c"));
	}

	@RenderedSnippetTest
	void allUndocumentedRequestFieldsCanBeIgnored(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("b").description("Field b")), true)
			.document(operationBuilder.request("http://localhost").content("{\"a\": 5, \"b\": 4}").build());
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`b`", "`Number`", "Field b"));
	}

	@RenderedSnippetTest
	void allUndocumentedFieldsContinueToBeIgnoredAfterAddingDescriptors(OperationBuilder operationBuilder,
			AssertableSnippets snippets) throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("b").description("Field b")), true)
			.andWithPrefix("c.", fieldWithPath("d").description("Field d"))
			.document(
					operationBuilder.request("http://localhost").content("{\"a\":5,\"b\":4,\"c\":{\"d\": 3}}").build());
		assertThat(snippets.requestFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`b`", "`Number`", "Field b")
			.row("`c.d`", "`Number`", "Field d"));
	}

	@RenderedSnippetTest
	void missingOptionalRequestField(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("a.b").description("one").type(JsonFieldType.STRING).optional()))
			.document(operationBuilder.request("http://localhost").content("{}").build());
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a.b`", "`String`", "one"));
	}

	@RenderedSnippetTest
	void missingIgnoredOptionalRequestFieldDoesNotRequireAType(OperationBuilder operationBuilder,
			AssertableSnippets snippets) throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one").ignored().optional()))
			.document(operationBuilder.request("http://localhost").content("{}").build());
		assertThat(snippets.requestFields()).isTable((table) -> table.withHeader("Path", "Type", "Description"));
	}

	@RenderedSnippetTest
	void presentOptionalRequestField(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("a.b").description("one").type(JsonFieldType.STRING).optional()))
			.document(operationBuilder.request("http://localhost").content("{\"a\": { \"b\": \"bravo\"}}").build());
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a.b`", "`String`", "one"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "request-fields", template = "request-fields-with-title")
	void requestFieldsWithCustomAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")),
				attributes(key("title").value("Custom title")))
			.document(operationBuilder.request("http://localhost").content("{\"a\": \"foo\"}").build());
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withTitleAndHeader("Custom title", "Path", "Type", "Description")
				.row("a", "String", "one"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "request-fields", template = "request-fields-with-extra-column")
	void requestFieldsWithCustomDescriptorAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("a.b").description("one").attributes(key("foo").value("alpha")),
						fieldWithPath("a.c").description("two").attributes(key("foo").value("bravo")),
						fieldWithPath("a").description("three").attributes(key("foo").value("charlie"))))
			.document(operationBuilder.request("http://localhost")
				.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
				.build());
		assertThat(snippets.requestFields()).isTable((table) -> table.withHeader("Path", "Type", "Description", "Foo")
			.row("a.b", "Number", "one", "alpha")
			.row("a.c", "String", "two", "bravo")
			.row("a", "Object", "three", "charlie"));
	}

	@RenderedSnippetTest
	void fieldWithExplicitExactlyMatchingType(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one").type(JsonFieldType.NUMBER)))
			.document(operationBuilder.request("http://localhost").content("{\"a\": 5 }").build());
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a`", "`Number`", "one"));
	}

	@RenderedSnippetTest
	void fieldWithExplicitVariesType(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one").type(JsonFieldType.VARIES)))
			.document(operationBuilder.request("http://localhost").content("{\"a\": 5 }").build());
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a`", "`Varies`", "one"));
	}

	@RenderedSnippetTest
	void applicationXmlRequestFields(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		xmlRequestFields(MediaType.APPLICATION_XML, operationBuilder, snippets);
	}

	@RenderedSnippetTest
	void textXmlRequestFields(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		xmlRequestFields(MediaType.TEXT_XML, operationBuilder, snippets);
	}

	@RenderedSnippetTest
	void customXmlRequestFields(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		xmlRequestFields(MediaType.parseMediaType("application/vnd.com.example+xml"), operationBuilder, snippets);
	}

	private void xmlRequestFields(MediaType contentType, OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one").type("b"),
				fieldWithPath("a/c").description("two").type("c"), fieldWithPath("a").description("three").type("a")))
			.document(operationBuilder.request("http://localhost")
				.content("<a><b>5</b><c>charlie</c></a>")
				.header(HttpHeaders.CONTENT_TYPE, contentType.toString())
				.build());
		assertThat(snippets.requestFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a/b`", "`b`", "one")
			.row("`a/c`", "`c`", "two")
			.row("`a`", "`a`", "three"));
	}

	@RenderedSnippetTest
	void entireSubsectionOfXmlPayloadCanBeDocumented(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(Arrays.asList(subsectionWithPath("a").description("one").type("a")))
			.document(operationBuilder.request("http://localhost")
				.content("<a><b>5</b><c>charlie</c></a>")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.build());
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a`", "`a`", "one"));
	}

	@RenderedSnippetTest
	void additionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		PayloadDocumentation
			.requestFields(fieldWithPath("a.b").description("one"), fieldWithPath("a.c").description("two"))
			.and(fieldWithPath("a").description("three"))
			.document(operationBuilder.request("http://localhost")
				.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
				.build());
		assertThat(snippets.requestFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a.b`", "`Number`", "one")
			.row("`a.c`", "`String`", "two")
			.row("`a`", "`Object`", "three"));
	}

	@RenderedSnippetTest
	void prefixedAdditionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		PayloadDocumentation.requestFields(fieldWithPath("a").description("one"))
			.andWithPrefix("a.", fieldWithPath("b").description("two"), fieldWithPath("c").description("three"))
			.document(operationBuilder.request("http://localhost")
				.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
				.build());
		assertThat(snippets.requestFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a`", "`Object`", "one")
			.row("`a.b`", "`Number`", "two")
			.row("`a.c`", "`String`", "three"));
	}

	@RenderedSnippetTest
	void requestWithFieldsWithEscapedContent(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("Foo|Bar").type("one|two").description("three|four")))
			.document(operationBuilder.request("http://localhost").content("{\"Foo|Bar\": 5}").build());
		assertThat(snippets.requestFields()).isTable(
				(table) -> table.withHeader("Path", "Type", "Description").row("`Foo|Bar`", "`one|two`", "three|four"));
	}

	@RenderedSnippetTest
	void mapRequestWithVaryingKeysMatchedUsingWildcard(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("things.*.size").description("one"),
				fieldWithPath("things.*.type").description("two")))
			.document(operationBuilder.request("http://localhost")
				.content("{\"things\": {\"12abf\": {\"type\":" + "\"Whale\", \"size\": \"HUGE\"},"
						+ "\"gzM33\" : {\"type\": \"Screw\"," + "\"size\": \"SMALL\"}}}")
				.build());
		assertThat(snippets.requestFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`things.*.size`", "`String`", "one")
			.row("`things.*.type`", "`String`", "two"));
	}

	@RenderedSnippetTest
	void requestWithArrayContainingFieldThatIsSometimesNull(OperationBuilder operationBuilder,
			AssertableSnippets snippets) throws IOException {
		new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("assets[].name").description("one").type(JsonFieldType.STRING).optional()))
			.document(operationBuilder.request("http://localhost")
				.content("{\"assets\": [" + "{\"name\": \"sample1\"}, " + "{\"name\": null}, "
						+ "{\"name\": \"sample2\"}]}")
				.build());
		assertThat(snippets.requestFields()).isTable(
				(table) -> table.withHeader("Path", "Type", "Description").row("`assets[].name`", "`String`", "one"));
	}

	@RenderedSnippetTest
	void optionalFieldBeneathArrayThatIsSometimesAbsent(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER).optional(),
						fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
			.document(operationBuilder.request("http://localhost")
				.content("{\"a\":[{\"b\": 1,\"c\": 2}, " + "{\"c\": 2}, {\"b\": 1,\"c\": 2}]}")
				.build());
		assertThat(snippets.requestFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a[].b`", "`Number`", "one")
			.row("`a[].c`", "`Number`", "two"));
	}

	@RenderedSnippetTest
	void typeDeterminationDoesNotSetTypeOnDescriptor(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		FieldDescriptor descriptor = fieldWithPath("a.b").description("one");
		new RequestFieldsSnippet(Arrays.asList(descriptor))
			.document(operationBuilder.request("http://localhost").content("{\"a\": {\"b\": 5}}").build());
		assertThat(descriptor.getType()).isNull();
		assertThat(snippets.requestFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a.b`", "`Number`", "one"));
	}

	@SnippetTest
	void undocumentedRequestField(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(operationBuilder.request("http://localhost").content("{\"a\": 5}").build()))
			.withMessageStartingWith("The following parts of the payload were not documented:");
	}

	@SnippetTest
	void missingRequestField(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")))
				.document(operationBuilder.request("http://localhost").content("{}").build()))
			.withMessage("Fields with the following paths were not found in the payload: [a.b]");
	}

	@SnippetTest
	void missingOptionalRequestFieldWithNoTypeProvided(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(FieldTypeRequiredException.class).isThrownBy(
				() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one").optional()))
					.document(operationBuilder.request("http://localhost").content("{ }").build()));
	}

	@SnippetTest
	void undocumentedRequestFieldAndMissingRequestField(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")))
				.document(operationBuilder.request("http://localhost").content("{ \"a\": { \"c\": 5 }}").build()))
			.withMessageStartingWith("The following parts of the payload were not documented:")
			.withMessageEndingWith("Fields with the following paths were not found in the payload: [a.b]");
	}

	@SnippetTest
	void attemptToDocumentFieldsWithNoRequestBody(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
				.document(operationBuilder.request("http://localhost").build()))
			.withMessage("Cannot document request fields as the request body is empty");
	}

	@SnippetTest
	void fieldWithExplicitTypeThatDoesNotMatchThePayload(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(FieldTypesDoNotMatchException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(
					Arrays.asList(fieldWithPath("a").description("one").type(JsonFieldType.OBJECT)))
				.document(operationBuilder.request("http://localhost").content("{ \"a\": 5 }").build()))
			.withMessage("The documented type of the field 'a' is Object but the actual type is Number");
	}

	@SnippetTest
	void fieldWithExplicitSpecificTypeThatActuallyVaries(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(FieldTypesDoNotMatchException.class).isThrownBy(() -> new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("[].a").description("one").type(JsonFieldType.OBJECT)))
			.document(operationBuilder.request("http://localhost").content("[{ \"a\": 5 },{ \"a\": \"b\" }]").build()))
			.withMessage("The documented type of the field '[].a' is Object but the actual type is Varies");
	}

	@SnippetTest
	void undocumentedXmlRequestField(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(operationBuilder.request("http://localhost")
					.content("<a><b>5</b></a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()))
			.withMessageStartingWith("The following parts of the payload were not documented:");
	}

	@SnippetTest
	void xmlDescendentsAreNotDocumentedByFieldDescriptor(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").type("a").description("one")))
				.document(operationBuilder.request("http://localhost")
					.content("<a><b>5</b></a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()))
			.withMessageStartingWith("The following parts of the payload were not documented:");
	}

	@SnippetTest
	void xmlRequestFieldWithNoType(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(FieldTypeRequiredException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
				.document(operationBuilder.request("http://localhost")
					.content("<a>5</a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()));
	}

	@SnippetTest
	void missingXmlRequestField(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(
					Arrays.asList(fieldWithPath("a/b").description("one"), fieldWithPath("a").description("one")))
				.document(operationBuilder.request("http://localhost")
					.content("<a></a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()))
			.withMessage("Fields with the following paths were not found in the payload: [a/b]");
	}

	@SnippetTest
	void undocumentedXmlRequestFieldAndMissingXmlRequestField(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one")))
				.document(operationBuilder.request("http://localhost")
					.content("<a><c>5</c></a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()))
			.withMessageStartingWith("The following parts of the payload were not documented:")
			.withMessageEndingWith("Fields with the following paths were not found in the payload: [a/b]");
	}

	@SnippetTest
	void unsupportedContent(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(PayloadHandlingException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(operationBuilder.request("http://localhost")
					.content("Some plain text")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
					.build()))
			.withMessage("Cannot handle text/plain content as it could not be parsed as JSON or XML");
	}

	@SnippetTest
	void nonOptionalFieldBeneathArrayThatIsSometimesNull(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(
					Arrays.asList(fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER),
							fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
				.document(operationBuilder.request("http://localhost")
					.content("{\"a\":[{\"b\": 1,\"c\": 2}, " + "{\"b\": null, \"c\": 2}," + " {\"b\": 1,\"c\": 2}]}")
					.build()))
			.withMessageStartingWith("Fields with the following paths were not found in the payload: [a[].b]");
	}

	@SnippetTest
	void nonOptionalFieldBeneathArrayThatIsSometimesAbsent(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(
					Arrays.asList(fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER),
							fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
				.document(operationBuilder.request("http://localhost")
					.content("{\"a\":[{\"b\": 1,\"c\": 2}, " + "{\"c\": 2}, {\"b\": 1,\"c\": 2}]}")
					.build()))
			.withMessageStartingWith("Fields with the following paths were not found in the payload: [a[].b]");
	}

}
