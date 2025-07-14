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
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link ResponseFieldsSnippet}.
 *
 * @author Andy Wilkinson
 * @author Sungjun Lee
 */
public class ResponseFieldsSnippetTests {

	@RenderedSnippetTest
	void mapResponseWithFields(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("id").description("one"),
				fieldWithPath("date").description("two"), fieldWithPath("assets").description("three"),
				fieldWithPath("assets[]").description("four"), fieldWithPath("assets[].id").description("five"),
				fieldWithPath("assets[].name").description("six")))
			.document(operationBuilder.response()
				.content("{\"id\": 67,\"date\": \"2015-01-20\",\"assets\":" + " [{\"id\":356,\"name\": \"sample\"}]}")
				.build());
		assertThat(snippets.responseFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`id`", "`Number`", "one")
			.row("`date`", "`String`", "two")
			.row("`assets`", "`Array`", "three")
			.row("`assets[]`", "`Array`", "four")
			.row("`assets[].id`", "`Number`", "five")
			.row("`assets[].name`", "`String`", "six"));
	}

	@RenderedSnippetTest
	void mapResponseWithNullField(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")))
			.document(operationBuilder.response().content("{\"a\": {\"b\": null}}").build());
		assertThat(snippets.responseFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a.b`", "`Null`", "one"));
	}

	@RenderedSnippetTest
	void subsectionOfMapResponse(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		responseFields(beneathPath("a"), fieldWithPath("b").description("one"), fieldWithPath("c").description("two"))
			.document(operationBuilder.response().content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}").build());
		assertThat(snippets.responseFields("beneath-a"))
			.isTable((table) -> table.withHeader("Path", "Type", "Description")
				.row("`b`", "`Number`", "one")
				.row("`c`", "`String`", "two"));
	}

	@RenderedSnippetTest
	void subsectionOfMapResponseBeneathAnArray(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		responseFields(beneathPath("a.b.[]"), fieldWithPath("c").description("one"),
				fieldWithPath("d.[].e").description("two"))
			.document(operationBuilder.response()
				.content("{\"a\": {\"b\": [{\"c\": 1, \"d\": [{\"e\": 5}]}, {\"c\": 3, \"d\": [{\"e\": 4}]}]}}")
				.build());
		assertThat(snippets.responseFields("beneath-a.b.[]"))
			.isTable((table) -> table.withHeader("Path", "Type", "Description")
				.row("`c`", "`Number`", "one")
				.row("`d.[].e`", "`Number`", "two"));
	}

	@RenderedSnippetTest
	void subsectionOfMapResponseWithCommonsPrefix(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		responseFields(beneathPath("a")).andWithPrefix("b.", fieldWithPath("c").description("two"))
			.document(operationBuilder.response().content("{\"a\": {\"b\": {\"c\": \"charlie\"}}}").build());
		assertThat(snippets.responseFields("beneath-a"))
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`b.c`", "`String`", "two"));
	}

	@RenderedSnippetTest
	void arrayResponseWithFields(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("[]a.b").description("one"),
				fieldWithPath("[]a.c").description("two"), fieldWithPath("[]a").description("three")))
			.document(operationBuilder.response()
				.content("[{\"a\": {\"b\": 5, \"c\":\"charlie\"}}," + "{\"a\": {\"b\": 4, \"c\":\"chalk\"}}]")
				.build());
		assertThat(snippets.responseFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`[]a.b`", "`Number`", "one")
			.row("`[]a.c`", "`String`", "two")
			.row("`[]a`", "`Object`", "three"));
	}

	@RenderedSnippetTest
	void arrayResponseWithAlwaysNullField(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("[]a.b").description("one"))).document(
				operationBuilder.response().content("[{\"a\": {\"b\": null}}," + "{\"a\": {\"b\": null}}]").build());
		assertThat(snippets.responseFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`[]a.b`", "`Null`", "one"));
	}

	@RenderedSnippetTest
	void arrayResponse(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("[]").description("one")))
			.document(operationBuilder.response().content("[\"a\", \"b\", \"c\"]").build());
		assertThat(snippets.responseFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`[]`", "`Array`", "one"));
	}

	@RenderedSnippetTest
	void ignoredResponseField(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a").ignored(), fieldWithPath("b").description("Field b")))
			.document(operationBuilder.response().content("{\"a\": 5, \"b\": 4}").build());
		assertThat(snippets.responseFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`b`", "`Number`", "Field b"));
	}

	@RenderedSnippetTest
	void allUndocumentedFieldsCanBeIgnored(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("b").description("Field b")), true)
			.document(operationBuilder.response().content("{\"a\": 5, \"b\": 4}").build());
		assertThat(snippets.responseFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`b`", "`Number`", "Field b"));
	}

	@RenderedSnippetTest
	void allUndocumentedFieldsContinueToBeIgnoredAfterAddingDescriptors(OperationBuilder operationBuilder,
			AssertableSnippets snippets) throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("b").description("Field b")), true)
			.andWithPrefix("c.", fieldWithPath("d").description("Field d"))
			.document(operationBuilder.response().content("{\"a\":5,\"b\":4,\"c\":{\"d\": 3}}").build());
		assertThat(snippets.responseFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`b`", "`Number`", "Field b")
			.row("`c.d`", "`Number`", "Field d"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "response-fields", template = "response-fields-with-title")
	void responseFieldsWithCustomAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")),
				attributes(key("title").value("Custom title")))
			.document(operationBuilder.response().content("{\"a\": \"foo\"}").build());
		assertThat(snippets.responseFields()).contains("Custom title");
	}

	@RenderedSnippetTest
	void missingOptionalResponseField(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a.b").description("one").type(JsonFieldType.STRING).optional()))
			.document(operationBuilder.response().content("{}").build());
		assertThat(snippets.responseFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a.b`", "`String`", "one"));
	}

	@RenderedSnippetTest
	void missingIgnoredOptionalResponseFieldDoesNotRequireAType(OperationBuilder operationBuilder,
			AssertableSnippets snippets) throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one").ignored().optional()))
			.document(operationBuilder.response().content("{}").build());
		assertThat(snippets.responseFields()).isTable((table) -> table.withHeader("Path", "Type", "Description"));
	}

	@RenderedSnippetTest
	void presentOptionalResponseField(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a.b").description("one").type(JsonFieldType.STRING).optional()))
			.document(operationBuilder.response().content("{\"a\": { \"b\": \"bravo\"}}").build());
		assertThat(snippets.responseFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a.b`", "`String`", "one"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "response-fields", template = "response-fields-with-extra-column")
	void responseFieldsWithCustomDescriptorAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a.b").description("one").attributes(key("foo").value("alpha")),
						fieldWithPath("a.c").description("two").attributes(key("foo").value("bravo")),
						fieldWithPath("a").description("three").attributes(key("foo").value("charlie"))))
			.document(operationBuilder.response().content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}").build());
		assertThat(snippets.responseFields()).isTable((table) -> table.withHeader("Path", "Type", "Description", "Foo")
			.row("a.b", "Number", "one", "alpha")
			.row("a.c", "String", "two", "bravo")
			.row("a", "Object", "three", "charlie"));
	}

	@RenderedSnippetTest
	void fieldWithExplicitExactlyMatchingType(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one").type(JsonFieldType.NUMBER)))
			.document(operationBuilder.response().content("{\"a\": 5 }").build());
		assertThat(snippets.responseFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a`", "`Number`", "one"));
	}

	@RenderedSnippetTest
	void fieldWithExplicitVariesType(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one").type(JsonFieldType.VARIES)))
			.document(operationBuilder.response().content("{\"a\": 5 }").build());
		assertThat(snippets.responseFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a`", "`Varies`", "one"));
	}

	@RenderedSnippetTest
	void applicationXmlResponseFields(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		xmlResponseFields(MediaType.APPLICATION_XML, operationBuilder, snippets);
	}

	@RenderedSnippetTest
	void textXmlResponseFields(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		xmlResponseFields(MediaType.TEXT_XML, operationBuilder, snippets);
	}

	@RenderedSnippetTest
	void customXmlResponseFields(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		xmlResponseFields(MediaType.parseMediaType("application/vnd.com.example+xml"), operationBuilder, snippets);
	}

	private void xmlResponseFields(MediaType contentType, OperationBuilder operationBuilder,
			AssertableSnippets snippets) throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one").type("b"),
				fieldWithPath("a/c").description("two").type("c"), fieldWithPath("a").description("three").type("a")))
			.document(operationBuilder.response()
				.content("<a><b>5</b><c>charlie</c></a>")
				.header(HttpHeaders.CONTENT_TYPE, contentType.toString())
				.build());
		assertThat(snippets.responseFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a/b`", "`b`", "one")
			.row("`a/c`", "`c`", "two")
			.row("`a`", "`a`", "three"));
	}

	@RenderedSnippetTest
	void xmlAttribute(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one").type("b"),
				fieldWithPath("a/@id").description("two").type("c")))
			.document(operationBuilder.response()
				.content("<a id=\"1\">foo</a>")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.build());
		assertThat(snippets.responseFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a`", "`b`", "one")
			.row("`a/@id`", "`c`", "two"));
	}

	@RenderedSnippetTest
	void missingOptionalXmlAttribute(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one").type("b"),
				fieldWithPath("a/@id").description("two").type("c").optional()))
			.document(operationBuilder.response()
				.content("<a>foo</a>")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.build());
		assertThat(snippets.responseFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a`", "`b`", "one")
			.row("`a/@id`", "`c`", "two"));
	}

	@RenderedSnippetTest
	void undocumentedAttributeDoesNotCauseFailure(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one").type("a")))
			.document(operationBuilder.response()
				.content("<a id=\"foo\">bar</a>")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.build());
		assertThat(snippets.responseFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`a`", "`a`", "one"));
	}

	@RenderedSnippetTest
	void additionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		PayloadDocumentation
			.responseFields(fieldWithPath("id").description("one"), fieldWithPath("date").description("two"),
					fieldWithPath("assets").description("three"))
			.and(fieldWithPath("assets[]").description("four"), fieldWithPath("assets[].id").description("five"),
					fieldWithPath("assets[].name").description("six"))
			.document(operationBuilder.response()
				.content("{\"id\": 67,\"date\": \"2015-01-20\",\"assets\":" + " [{\"id\":356,\"name\": \"sample\"}]}")
				.build());
		assertThat(snippets.responseFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`id`", "`Number`", "one")
			.row("`date`", "`String`", "two")
			.row("`assets`", "`Array`", "three")
			.row("`assets[]`", "`Array`", "four")
			.row("`assets[].id`", "`Number`", "five")
			.row("`assets[].name`", "`String`", "six"));
	}

	@RenderedSnippetTest
	void prefixedAdditionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		PayloadDocumentation.responseFields(fieldWithPath("a").description("one"))
			.andWithPrefix("a.", fieldWithPath("b").description("two"), fieldWithPath("c").description("three"))
			.document(operationBuilder.response().content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}").build());
		assertThat(snippets.responseFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a`", "`Object`", "one")
			.row("`a.b`", "`Number`", "two")
			.row("`a.c`", "`String`", "three"));
	}

	@RenderedSnippetTest
	void responseWithFieldsWithEscapedContent(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("Foo|Bar").type("one|two").description("three|four")))
			.document(operationBuilder.response().content("{\"Foo|Bar\": 5}").build());
		assertThat(snippets.responseFields()).isTable(
				(table) -> table.withHeader("Path", "Type", "Description").row("`Foo|Bar`", "`one|two`", "three|four"));
	}

	@RenderedSnippetTest
	void mapResponseWithVaryingKeysMatchedUsingWildcard(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("things.*.size").description("one"),
				fieldWithPath("things.*.type").description("two")))
			.document(operationBuilder.response()
				.content("{\"things\": {\"12abf\": {\"type\":" + "\"Whale\", \"size\": \"HUGE\"},"
						+ "\"gzM33\" : {\"type\": \"Screw\"," + "\"size\": \"SMALL\"}}}")
				.build());
		assertThat(snippets.responseFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`things.*.size`", "`String`", "one")
			.row("`things.*.type`", "`String`", "two"));
	}

	@RenderedSnippetTest
	void responseWithArrayContainingFieldThatIsSometimesNull(OperationBuilder operationBuilder,
			AssertableSnippets snippets) throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("assets[].name").description("one").type(JsonFieldType.STRING).optional()))
			.document(operationBuilder.response()
				.content("{\"assets\": [" + "{\"name\": \"sample1\"}, " + "{\"name\": null}, "
						+ "{\"name\": \"sample2\"}]}")
				.build());
		assertThat(snippets.responseFields()).isTable(
				(table) -> table.withHeader("Path", "Type", "Description").row("`assets[].name`", "`String`", "one"));
	}

	@RenderedSnippetTest
	void optionalFieldBeneathArrayThatIsSometimesAbsent(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER).optional(),
						fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
			.document(operationBuilder.response()
				.content("{\"a\":[{\"b\": 1,\"c\": 2}, " + "{\"c\": 2}, {\"b\": 1,\"c\": 2}]}")
				.build());
		assertThat(snippets.responseFields()).isTable((table) -> table.withHeader("Path", "Type", "Description")
			.row("`a[].b`", "`Number`", "one")
			.row("`a[].c`", "`Number`", "two"));
	}

	@RenderedSnippetTest
	void typeDeterminationDoesNotSetTypeOnDescriptor(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		FieldDescriptor descriptor = fieldWithPath("id").description("one");
		new ResponseFieldsSnippet(Arrays.asList(descriptor))
			.document(operationBuilder.response().content("{\"id\": 67}").build());
		assertThat(descriptor.getType()).isNull();
		assertThat(snippets.responseFields())
			.isTable((table) -> table.withHeader("Path", "Type", "Description").row("`id`", "`Number`", "one"));
	}

	@SnippetTest
	void attemptToDocumentFieldsWithNoResponseBody(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
				.document(operationBuilder.build()))
			.withMessage("Cannot document response fields as the response body is empty");
	}

	@SnippetTest
	void fieldWithExplicitTypeThatDoesNotMatchThePayload(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(FieldTypesDoNotMatchException.class)
			.isThrownBy(() -> new ResponseFieldsSnippet(
					Arrays.asList(fieldWithPath("a").description("one").type(JsonFieldType.OBJECT)))
				.document(operationBuilder.response().content("{ \"a\": 5 }}").build()))
			.withMessage("The documented type of the field 'a' is Object but the actual type is Number");
	}

	@SnippetTest
	void fieldWithExplicitSpecificTypeThatActuallyVaries(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(FieldTypesDoNotMatchException.class)
			.isThrownBy(() -> new ResponseFieldsSnippet(
					Arrays.asList(fieldWithPath("[].a").description("one").type(JsonFieldType.OBJECT)))
				.document(operationBuilder.response().content("[{ \"a\": 5 },{ \"a\": \"b\" }]").build()))
			.withMessage("The documented type of the field '[].a' is Object but the actual type is Varies");
	}

	@SnippetTest
	void undocumentedXmlResponseField(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new ResponseFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(operationBuilder.response()
					.content("<a><b>5</b></a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()))
			.withMessageStartingWith("The following parts of the payload were not documented:");
	}

	@SnippetTest
	void missingXmlAttribute(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one").type("b"),
					fieldWithPath("a/@id").description("two").type("c")))
				.document(operationBuilder.response()
					.content("<a>foo</a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()))
			.withMessage("Fields with the following paths were not found in the payload: [a/@id]");
	}

	@SnippetTest
	void documentedXmlAttributesAreRemoved(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(
					() -> new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a/@id").description("one").type("a")))
						.document(operationBuilder.response()
							.content("<a id=\"foo\">bar</a>")
							.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
							.build()))
			.withMessage(String.format("The following parts of the payload were not documented:%n<a>bar</a>%n"));
	}

	@SnippetTest
	void xmlResponseFieldWithNoType(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(FieldTypeRequiredException.class)
			.isThrownBy(() -> new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
				.document(operationBuilder.response()
					.content("<a>5</a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()));
	}

	@SnippetTest
	void missingXmlResponseField(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new ResponseFieldsSnippet(
					Arrays.asList(fieldWithPath("a/b").description("one"), fieldWithPath("a").description("one")))
				.document(operationBuilder.response()
					.content("<a></a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()))
			.withMessage("Fields with the following paths were not found in the payload: [a/b]");
	}

	@SnippetTest
	void undocumentedXmlResponseFieldAndMissingXmlResponseField(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one")))
				.document(operationBuilder.response()
					.content("<a><c>5</c></a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()))
			.withMessageStartingWith("The following parts of the payload were not documented:")
			.withMessageEndingWith("Fields with the following paths were not found in the payload: [a/b]");
	}

	@SnippetTest
	void unsupportedContent(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(PayloadHandlingException.class)
			.isThrownBy(() -> new ResponseFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(operationBuilder.response()
					.content("Some plain text")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
					.build()))
			.withMessage("Cannot handle text/plain content as it could not be parsed as JSON or XML");
	}

	@SnippetTest
	void nonOptionalFieldBeneathArrayThatIsSometimesNull(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new ResponseFieldsSnippet(
					Arrays.asList(fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER),
							fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
				.document(operationBuilder.response()
					.content("{\"a\":[{\"b\": 1,\"c\": 2}, " + "{\"b\": null, \"c\": 2}," + " {\"b\": 1,\"c\": 2}]}")
					.build()))
			.withMessageStartingWith("Fields with the following paths were not found in the payload: [a[].b]");
	}

	@SnippetTest
	void nonOptionalFieldBeneathArrayThatIsSometimesAbsent(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new ResponseFieldsSnippet(
					Arrays.asList(fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER),
							fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
				.document(operationBuilder.response()
					.content("{\"a\":[{\"b\": 1,\"c\": 2}, " + "{\"c\": 2}, {\"b\": 1,\"c\": 2}]}")
					.build()))
			.withMessageStartingWith("Fields with the following paths were not found in the payload: [a[].b]");
	}

}
