/*
 * Copyright 2014-2015 the original author or authors.
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
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;
import org.springframework.restdocs.test.OperationBuilder;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;

/**
 * Tests for {@link RequestFieldsSnippet}.
 *
 * @author Andy Wilkinson
 */
public class RequestFieldsSnippetTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Rule
	public final ExpectedSnippet snippet = new ExpectedSnippet();

	@Test
	public void mapRequestWithFields() throws IOException {
		this.snippet.expectRequestFields("map-request-with-fields").withContents(
				tableWithHeader("Path", "Type", "Description")
						.row("a.b", "Number", "one").row("a.c", "String", "two")
						.row("a", "Object", "three"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one"),
				fieldWithPath("a.c").description("two"),
				fieldWithPath("a").description("three"))).document(new OperationBuilder(
				"map-request-with-fields", this.snippet.getOutputDirectory())
				.request("http://localhost")
				.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}").build());
	}

	@Test
	public void arrayRequestWithFields() throws IOException {
		this.snippet.expectRequestFields("array-request-with-fields").withContents(
				tableWithHeader("Path", "Type", "Description")
						.row("[]a.b", "Number", "one").row("[]a.c", "String", "two")
						.row("[]a", "Object", "three"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("[]a.b").description("one"),
				fieldWithPath("[]a.c").description("two"), fieldWithPath("[]a")
						.description("three"))).document(new OperationBuilder(
				"array-request-with-fields", this.snippet.getOutputDirectory())
				.request("http://localhost")
				.content("[{\"a\": {\"b\": 5}},{\"a\": {\"c\": \"charlie\"}}]").build());
	}

	@Test
	public void undocumentedRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(startsWith("The following parts of the payload were not"
						+ " documented:"));
		new RequestFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(new OperationBuilder("undocumented-request-field", this.snippet
						.getOutputDirectory()).request("http://localhost")
						.content("{\"a\": 5}").build());
	}

	@Test
	public void missingRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Fields with the following paths were not found"
						+ " in the payload: [a.b]"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")))
				.document(new OperationBuilder("missing-request-fields", this.snippet
						.getOutputDirectory()).request("http://localhost").content("{}")
						.build());
	}

	@Test
	public void missingOptionalRequestFieldWithNoTypeProvided() throws IOException {
		this.thrown.expect(FieldTypeRequiredException.class);
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")
				.optional())).document(new OperationBuilder(
				"missing-optional-request-field-with-no-type", this.snippet
						.getOutputDirectory()).request("http://localhost").content("{ }")
				.build());
	}

	@Test
	public void undocumentedRequestFieldAndMissingRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(startsWith("The following parts of the payload were not"
						+ " documented:"));
		this.thrown
				.expectMessage(endsWith("Fields with the following paths were not found"
						+ " in the payload: [a.b]"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")))
				.document(new OperationBuilder(
						"undocumented-request-field-and-missing-request-field",
						this.snippet.getOutputDirectory()).request("http://localhost")
						.content("{ \"a\": { \"c\": 5 }}").build());
	}

	@Test
	public void requestFieldsWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-fields")).willReturn(
				snippetResource("request-fields-with-extra-column"));
		this.snippet.expectRequestFields(
				"request-fields-with-custom-descriptor-attributes").withContents(
				tableWithHeader("Path", "Type", "Description", "Foo")
						.row("a.b", "Number", "one", "alpha")
						.row("a.c", "String", "two", "bravo")
						.row("a", "Object", "three", "charlie"));

		new RequestFieldsSnippet(Arrays.asList(
				fieldWithPath("a.b").description("one").attributes(
						key("foo").value("alpha")),
				fieldWithPath("a.c").description("two").attributes(
						key("foo").value("bravo")),
				fieldWithPath("a").description("three").attributes(
						key("foo").value("charlie")))).document(new OperationBuilder(
				"request-fields-with-custom-descriptor-attributes", this.snippet
						.getOutputDirectory())
				.attribute(TemplateEngine.class.getName(),
						new MustacheTemplateEngine(resolver)).request("http://localhost")
				.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}").build());
	}

	@Test
	public void requestFieldsWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-fields")).willReturn(
				snippetResource("request-fields-with-title"));
		this.snippet.expectRequestFields("request-fields-with-custom-attributes")
				.withContents(startsWith(".Custom title"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")),
				attributes(key("title").value("Custom title")))
				.document(new OperationBuilder("request-fields-with-custom-attributes",
						this.snippet.getOutputDirectory())
						.attribute(TemplateEngine.class.getName(),
								new MustacheTemplateEngine(resolver))
						.request("http://localhost").content("{\"a\": \"foo\"}").build());
	}

	@Test
	public void requestFieldsWithListDescription() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-fields")).willReturn(
				snippetResource("request-fields-with-list-description"));
		this.snippet.expectRequestFields("request-fields-with-list-description")
				.withContents(
						tableWithHeader("Path", "Type", "Description")
								//
								.row("a", "String", String.format(" - one%n - two"))
								.configuration("[cols=\"1,1,1a\"]"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description(
				Arrays.asList("one", "two"))))
				.document(new OperationBuilder("request-fields-with-list-description",
						this.snippet.getOutputDirectory())
						.attribute(TemplateEngine.class.getName(),
								new MustacheTemplateEngine(resolver))
						.request("http://localhost").content("{\"a\": \"foo\"}").build());
	}

	@Test
	public void xmlRequestFields() throws IOException {
		this.snippet.expectRequestFields("xml-request").withContents(
				tableWithHeader("Path", "Type", "Description").row("a/b", "b", "one")
						.row("a/c", "c", "two").row("a", "a", "three"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one")
				.type("b"), fieldWithPath("a/c").description("two").type("c"),
				fieldWithPath("a").description("three").type("a")))
				.document(new OperationBuilder("xml-request", this.snippet
						.getOutputDirectory())
						.request("http://localhost")
						.content("<a><b>5</b><c>charlie</c></a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	@Test
	public void undocumentedXmlRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(startsWith("The following parts of the payload were not"
						+ " documented:"));
		new RequestFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(new OperationBuilder("undocumented-xml-request-field",
						this.snippet.getOutputDirectory())
						.request("http://localhost")
						.content("<a><b>5</b></a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	@Test
	public void xmlRequestFieldWithNoType() throws IOException {
		this.thrown.expect(FieldTypeRequiredException.class);
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
				.document(new OperationBuilder("missing-xml-request", this.snippet
						.getOutputDirectory())
						.request("http://localhost")
						.content("<a>5</a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	@Test
	public void missingXmlRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Fields with the following paths were not found"
						+ " in the payload: [a/b]"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one"),
				fieldWithPath("a").description("one"))).document(new OperationBuilder(
				"missing-xml-request-fields", this.snippet.getOutputDirectory())
				.request("http://localhost").content("<a></a>")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.build());
	}

	@Test
	public void undocumentedXmlRequestFieldAndMissingXmlRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(startsWith("The following parts of the payload were not"
						+ " documented:"));
		this.thrown
				.expectMessage(endsWith("Fields with the following paths were not found"
						+ " in the payload: [a/b]"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one")))
				.document(new OperationBuilder(
						"undocumented-xml-request-field-and-missing-xml-request-field",
						this.snippet.getOutputDirectory())
						.request("http://localhost")
						.content("<a><c>5</c></a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	private FileSystemResource snippetResource(String name) {
		return new FileSystemResource("src/test/resources/custom-snippet-templates/"
				+ name + ".snippet");
	}

}
