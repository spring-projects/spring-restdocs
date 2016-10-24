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
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link RequestFieldsSnippet}.
 *
 * @author Andy Wilkinson
 */
public class RequestFieldsSnippetTests extends AbstractSnippetTests {

	public RequestFieldsSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void mapRequestWithFields() throws IOException {
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`a.b`", "`Number`", "one").row("`a.c`", "`String`", "two")
						.row("`a`", "`Object`", "three"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one"),
				fieldWithPath("a.c").description("two"),
				fieldWithPath("a").description("three")))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
								.build());
	}

	@Test
	public void arrayRequestWithFields() throws IOException {
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`[]`", "`Array`", "one").row("`[]a.b`", "`Number`", "two")
						.row("`[]a.c`", "`String`", "three")
						.row("`[]a`", "`Object`", "four"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("[]").description("one"),
				fieldWithPath("[]a.b").description("two"),
				fieldWithPath("[]a.c").description("three"),
				fieldWithPath("[]a").description("four")))
						.document(this.operationBuilder.request("http://localhost")
								.content(
										"[{\"a\": {\"b\": 5}},{\"a\": {\"c\": \"charlie\"}}]")
								.build());
	}

	@Test
	public void ignoredRequestField() throws IOException {
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`b`",
						"`Number`", "Field b"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").ignored(),
				fieldWithPath("b").description("Field b")))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\": 5, \"b\": 4}").build());
	}

	@Test
	public void allUndocumentedRequestFieldsCanBeIgnored() throws IOException {
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`b`",
						"`Number`", "Field b"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("b").description("Field b")),
				true).document(
						this.operationBuilder.request("http://localhost")
								.content("{\"a\": 5, \"b\": 4}").build());
	}

	@Test
	public void missingOptionalRequestField() throws IOException {
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`a.b`",
						"`String`", "one"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")
				.type(JsonFieldType.STRING).optional()))
						.document(this.operationBuilder.request("http://localhost")
								.content("{}").build());
	}

	@Test
	public void missingIgnoredOptionalRequestFieldDoesNotRequireAType()
			throws IOException {
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description"));
		new RequestFieldsSnippet(Arrays
				.asList(fieldWithPath("a.b").description("one").ignored().optional()))
						.document(this.operationBuilder.request("http://localhost")
								.content("{}").build());
	}

	@Test
	public void presentOptionalRequestField() throws IOException {
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`a.b`",
						"`String`", "one"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")
				.type(JsonFieldType.STRING).optional()))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\": { \"b\": \"bravo\"}}").build());
	}

	@Test
	public void requestFieldsWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-fields"))
				.willReturn(snippetResource("request-fields-with-title"));
		this.snippet.expectRequestFields().withContents(containsString("Custom title"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")),
				attributes(
						key("title").value("Custom title")))
								.document(
										this.operationBuilder
												.attribute(TemplateEngine.class.getName(),
														new MustacheTemplateEngine(
																resolver))
												.request("http://localhost")
												.content("{\"a\": \"foo\"}").build());
	}

	@Test
	public void requestFieldsWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-fields"))
				.willReturn(snippetResource("request-fields-with-extra-column"));
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description", "Foo")
						.row("a.b", "Number", "one", "alpha")
						.row("a.c", "String", "two", "bravo")
						.row("a", "Object", "three", "charlie"));

		new RequestFieldsSnippet(Arrays.asList(
				fieldWithPath("a.b").description("one")
						.attributes(key("foo").value("alpha")),
				fieldWithPath("a.c").description("two")
						.attributes(key("foo").value("bravo")),
				fieldWithPath("a").description("three")
						.attributes(key("foo").value("charlie"))))
								.document(
										this.operationBuilder
												.attribute(TemplateEngine.class.getName(),
														new MustacheTemplateEngine(
																resolver))
												.request("http://localhost")
												.content(
														"{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
												.build());
	}

	@Test
	public void fieldWithExplictExactlyMatchingType() throws IOException {
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`a`",
						"`Number`", "one"));

		new RequestFieldsSnippet(Arrays
				.asList(fieldWithPath("a").description("one").type(JsonFieldType.NUMBER)))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\": 5 }").build());
	}

	@Test
	public void fieldWithExplictVariesType() throws IOException {
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`a`",
						"`Varies`", "one"));

		new RequestFieldsSnippet(Arrays
				.asList(fieldWithPath("a").description("one").type(JsonFieldType.VARIES)))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\": 5 }").build());
	}

	@Test
	public void xmlRequestFields() throws IOException {
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`a/b`", "`b`", "one").row("`a/c`", "`c`", "two").row("`a`",
								"`a`", "three"));

		new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("a/b").description("one").type("b"),
						fieldWithPath("a/c").description("two").type("c"),
						fieldWithPath("a").description("three").type("a")))
								.document(
										this.operationBuilder.request("http://localhost")
												.content("<a><b>5</b><c>charlie</c></a>")
												.header(HttpHeaders.CONTENT_TYPE,
														MediaType.APPLICATION_XML_VALUE)
												.build());
	}

	@Test
	public void additionalDescriptors() throws IOException {
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`a.b`", "`Number`", "one").row("`a.c`", "`String`", "two")
						.row("`a`", "`Object`", "three"));

		PayloadDocumentation
				.requestFields(fieldWithPath("a.b").description("one"),
						fieldWithPath("a.c").description("two"))
				.and(fieldWithPath("a").description("three"))
				.document(this.operationBuilder.request("http://localhost")
						.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}").build());
	}

	@Test
	public void prefixedAdditionalDescriptors() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-fields"))
				.willReturn(snippetResource("request-fields-with-extra-column"));

		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description", "Foo")
						.row("a", "Object", "one", "alpha")
						.row("a.b", "Number", "two", "bravo")
						.row("a.c", "String", "three", "charlie"));

		PayloadDocumentation.requestFields(
				fieldWithPath("a").description("one").attributes(key("foo").value("alpha")))
				.andWithPrefix("a.",
						fieldWithPath("b").description("two")
								.attributes(key("foo").value("bravo")),
						fieldWithPath("c").description("three")
								.attributes(key("foo").value("charlie")))
				.document(this.operationBuilder
								.attribute(TemplateEngine.class.getName(),
										new MustacheTemplateEngine(
												resolver))
								.request("http://localhost")
								.content(
										"{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
								.build());
	}

	@Test
	public void requestWithFieldsWithEscapedContent() throws IOException {
		this.snippet.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row(
						escapeIfNecessary("`Foo|Bar`"), escapeIfNecessary("`one|two`"),
						escapeIfNecessary("three|four")));

		new RequestFieldsSnippet(Arrays.asList(
				fieldWithPath("Foo|Bar").type("one|two").description("three|four")))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"Foo|Bar\": 5}").build());
	}

	private String escapeIfNecessary(String input) {
		if (this.templateFormat.equals(TemplateFormats.markdown())) {
			return input;
		}
		return input.replace("|", "\\|");
	}

}
