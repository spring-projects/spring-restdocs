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
		this.snippet.expectRequestFields("map-request-with-fields")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("a.b", "Number", "one").row("a.c", "String", "two")
						.row("a", "Object", "three"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one"),
				fieldWithPath("a.c").description("two"),
				fieldWithPath("a").description("three")))
						.document(operationBuilder("map-request-with-fields")
								.request("http://localhost")
								.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
								.build());
	}

	@Test
	public void arrayRequestWithFields() throws IOException {
		this.snippet.expectRequestFields("array-request-with-fields")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("[]a.b", "Number", "one").row("[]a.c", "String", "two")
						.row("[]a", "Object", "three"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("[]a.b").description("one"),
				fieldWithPath("[]a.c").description("two"),
				fieldWithPath("[]a").description("three")))
						.document(operationBuilder("array-request-with-fields")
								.request("http://localhost")
								.content(
										"[{\"a\": {\"b\": 5}},{\"a\": {\"c\": \"charlie\"}}]")
								.build());
	}

	@Test
	public void ignoredRequestField() throws IOException {
		this.snippet.expectRequestFields("ignored-request-field")
				.withContents(tableWithHeader("Path", "Type", "Description").row("b",
						"Number", "Field b"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").ignored(),
				fieldWithPath("b").description("Field b")))
						.document(operationBuilder("ignored-request-field")
								.request("http://localhost")
								.content("{\"a\": 5, \"b\": 4}").build());
	}

	@Test
	public void allUndocumentedRequestFieldsCanBeIgnored() throws IOException {
		this.snippet.expectRequestFields("ignore-all-undocumented")
				.withContents(tableWithHeader("Path", "Type", "Description").row("b",
						"Number", "Field b"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("b").description("Field b")),
				true).document(
						operationBuilder("ignore-all-undocumented")
								.request("http://localhost")
								.content("{\"a\": 5, \"b\": 4}").build());
	}

	@Test
	public void requestFieldsWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-fields"))
				.willReturn(snippetResource("request-fields-with-title"));
		this.snippet.expectRequestFields("request-fields-with-custom-attributes")
				.withContents(containsString("Custom title"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")),
				attributes(key("title").value("Custom title"))).document(
						operationBuilder("request-fields-with-custom-attributes")
								.attribute(TemplateEngine.class.getName(),
										new MustacheTemplateEngine(resolver))
								.request("http://localhost").content("{\"a\": \"foo\"}")
								.build());
	}

	@Test
	public void requestFieldsWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-fields"))
				.willReturn(snippetResource("request-fields-with-extra-column"));
		this.snippet
				.expectRequestFields("request-fields-with-custom-descriptor-attributes")
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
								.document(operationBuilder(
										"request-fields-with-custom-descriptor-attributes")
												.attribute(TemplateEngine.class.getName(),
														new MustacheTemplateEngine(
																resolver))
												.request("http://localhost")
												.content(
														"{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
										.build());
	}

	@Test
	public void xmlRequestFields() throws IOException {
		this.snippet.expectRequestFields("xml-request")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("a/b", "b", "one").row("a/c", "c", "two")
						.row("a", "a", "three"));

		new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("a/b").description("one").type("b"),
						fieldWithPath("a/c").description("two").type("c"),
						fieldWithPath("a").description("three").type("a")))
								.document(
										operationBuilder("xml-request")
												.request("http://localhost")
												.content("<a><b>5</b><c>charlie</c></a>")
												.header(HttpHeaders.CONTENT_TYPE,
														MediaType.APPLICATION_XML_VALUE)
								.build());
	}

	@Test
	public void additionalDescriptors() throws IOException {
		this.snippet.expectRequestFields("additional-descriptors")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("a.b", "Number", "one").row("a.c", "String", "two")
						.row("a", "Object", "three"));

		PayloadDocumentation
				.requestFields(fieldWithPath("a.b").description("one"),
						fieldWithPath("a.c").description("two"))
				.and(fieldWithPath("a").description("three"))
				.document(operationBuilder("additional-descriptors")
						.request("http://localhost")
						.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}").build());
	}

	@Test
	public void prefixedAdditionalDescriptors() throws IOException {
		this.snippet.expectRequestFields("prefixed-additional-descriptors")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("a", "Object", "one").row("a.b", "Number", "two")
						.row("a.c", "String", "three"));

		PayloadDocumentation.requestFields(fieldWithPath("a").description("one"))
				.andWithPrefix("a.", fieldWithPath("b").description("two"),
						fieldWithPath("c").description("three"))
				.document(operationBuilder("prefixed-additional-descriptors")
						.request("http://localhost")
						.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}").build());
	}

}
