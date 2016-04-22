/*
 * Copyright 2014-2016 the original author or authors.
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

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link ResponseFieldsSnippet}.
 *
 * @author Andy Wilkinson
 */
public class ResponseFieldsSnippetTests extends AbstractSnippetTests {

	public ResponseFieldsSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void mapResponseWithFields() throws IOException {
		this.snippet.expectResponseFields("map-response-with-fields")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("id", "Number", "one").row("date", "String", "two")
						.row("assets", "Array", "three").row("assets[]", "Object", "four")
						.row("assets[].id", "Number", "five")
						.row("assets[].name", "String", "six"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("id").description("one"),
				fieldWithPath("date").description("two"),
				fieldWithPath("assets").description("three"),
				fieldWithPath("assets[]").description("four"),
				fieldWithPath("assets[].id").description("five"),
				fieldWithPath("assets[].name").description("six")))
						.document(operationBuilder("map-response-with-fields").response()
								.content(
										"{\"id\": 67,\"date\": \"2015-01-20\",\"assets\":"
												+ " [{\"id\":356,\"name\": \"sample\"}]}")
								.build());
	}

	@Test
	public void arrayResponseWithFields() throws IOException {
		this.snippet.expectResponseFields("array-response-with-fields")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("[]a.b", "Number", "one").row("[]a.c", "String", "two")
						.row("[]a", "Object", "three"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("[]a.b").description("one"),
				fieldWithPath("[]a.c").description("two"),
				fieldWithPath("[]a").description("three"))).document(
						operationBuilder("array-response-with-fields").response()
								.content(
										"[{\"a\": {\"b\": 5}},{\"a\": {\"c\": \"charlie\"}}]")
										.build());
	}

	@Test
	public void arrayResponse() throws IOException {
		this.snippet.expectResponseFields("array-response")
				.withContents(tableWithHeader("Path", "Type", "Description").row("[]",
						"String", "one"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("[]").description("one")))
				.document(operationBuilder("array-response").response()
						.content("[\"a\", \"b\", \"c\"]").build());
	}

	@Test
	public void ignoredResponseField() throws IOException {
		this.snippet.expectResponseFields("ignored-response-field")
				.withContents(tableWithHeader("Path", "Type", "Description").row("b",
						"Number", "Field b"));

		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").ignored(),
				fieldWithPath("b").description("Field b")))
						.document(operationBuilder("ignored-response-field").response()
								.content("{\"a\": 5, \"b\": 4}").build());
	}

	@Test
	public void allUndocumentedFieldsCanBeIgnored() throws IOException {
		this.snippet.expectResponseFields("ignore-all-undocumented")
				.withContents(tableWithHeader("Path", "Type", "Description").row("b",
						"Number", "Field b"));

		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("b").description("Field b")), true)
						.document(operationBuilder("ignore-all-undocumented").response()
								.content("{\"a\": 5, \"b\": 4}").build());
	}

	@Test
	public void responseFieldsWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-fields"))
				.willReturn(snippetResource("response-fields-with-title"));
		this.snippet.expectResponseFields("response-fields-with-custom-attributes")
				.withContents(containsString("Custom title"));

		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")),
				attributes(key("title").value("Custom title"))).document(
						operationBuilder("response-fields-with-custom-attributes")
								.attribute(TemplateEngine.class.getName(),
										new MustacheTemplateEngine(resolver))
								.response().content("{\"a\": \"foo\"}").build());
	}

	@Test
	public void responseFieldsWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-fields"))
				.willReturn(snippetResource("response-fields-with-extra-column"));
		this.snippet.expectResponseFields("response-fields-with-custom-attributes")
				.withContents(tableWithHeader("Path", "Type", "Description", "Foo")
						.row("a.b", "Number", "one", "alpha")
						.row("a.c", "String", "two", "bravo")
						.row("a", "Object", "three", "charlie"));

		new ResponseFieldsSnippet(Arrays.asList(
				fieldWithPath("a.b").description("one")
						.attributes(key("foo").value("alpha")),
				fieldWithPath("a.c").description("two")
						.attributes(key("foo").value("bravo")),
				fieldWithPath("a").description("three")
						.attributes(key("foo").value("charlie")))).document(
								operationBuilder("response-fields-with-custom-attributes")
										.attribute(TemplateEngine.class.getName(),
												new MustacheTemplateEngine(resolver))
										.response()
										.content(
												"{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
										.build());
	}

	@Test
	public void xmlResponseFields() throws IOException {
		this.snippet.expectResponseFields("xml-response")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("a/b", "b", "one").row("a/c", "c", "two")
						.row("a", "a", "three"));
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a/b").description("one").type("b"),
						fieldWithPath("a/c").description("two").type("c"),
						fieldWithPath("a").description("three").type("a")))
								.document(
										operationBuilder("xml-response").response()
												.content("<a><b>5</b><c>charlie</c></a>")
												.header(HttpHeaders.CONTENT_TYPE,
														MediaType.APPLICATION_XML_VALUE)
										.build());
	}

	@Test
	public void xmlAttribute() throws IOException {
		this.snippet.expectResponseFields("xml-attribute")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("a", "b", "one").row("a/@id", "c", "two"));
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a").description("one").type("b"),
						fieldWithPath("a/@id").description("two").type("c")))
								.document(
										operationBuilder("xml-attribute").response()
												.content("<a id=\"1\">foo</a>")
												.header(HttpHeaders.CONTENT_TYPE,
														MediaType.APPLICATION_XML_VALUE)
										.build());
	}

	@Test
	public void missingOptionalXmlAttribute() throws IOException {
		this.snippet.expectResponseFields("missing-optional-xml-attribute")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("a", "b", "one").row("a/@id", "c", "two"));
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a").description("one").type("b"),
						fieldWithPath("a/@id").description("two").type("c").optional()))
								.document(
										operationBuilder("missing-optional-xml-attribute")
												.response().content("<a>foo</a>")
												.header(HttpHeaders.CONTENT_TYPE,
														MediaType.APPLICATION_XML_VALUE)
										.build());
	}

	@Test
	public void undocumentedAttributeDoesNotCauseFailure() throws IOException {
		this.snippet.expectResponseFields("undocumented-attribute").withContents(
				tableWithHeader("Path", "Type", "Description").row("a", "a", "one"));
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a").description("one").type("a")))
						.document(operationBuilder("undocumented-attribute").response()
								.content("<a id=\"foo\">bar</a>")
								.header(HttpHeaders.CONTENT_TYPE,
										MediaType.APPLICATION_XML_VALUE)
								.build());
	}

	@Test
	public void additionalDescriptors() throws IOException {
		this.snippet.expectResponseFields("additional-descriptors")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("id", "Number", "one").row("date", "String", "two")
						.row("assets", "Array", "three").row("assets[]", "Object", "four")
						.row("assets[].id", "Number", "five")
						.row("assets[].name", "String", "six"));
		PayloadDocumentation
				.responseFields(fieldWithPath("id").description("one"),
						fieldWithPath("date").description("two"),
						fieldWithPath("assets").description("three"))
				.and(fieldWithPath("assets[]").description("four"),
						fieldWithPath("assets[].id").description("five"),
						fieldWithPath("assets[].name").description("six"))
				.document(operationBuilder("additional-descriptors").response()
						.content("{\"id\": 67,\"date\": \"2015-01-20\",\"assets\":"
								+ " [{\"id\":356,\"name\": \"sample\"}]}")
						.build());
	}

	@Test
	public void prefixedAdditionalDescriptors() throws IOException {
		this.snippet.expectResponseFields("prefixed-additional-descriptors")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("a", "Object", "one").row("a.b", "Number", "two")
						.row("a.c", "String", "three"));

		PayloadDocumentation.responseFields(fieldWithPath("a").description("one"))
				.andWithPrefix("a.", fieldWithPath("b").description("two"),
						fieldWithPath("c").description("three"))
				.document(operationBuilder("prefixed-additional-descriptors").response()
						.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}").build());
	}

}
