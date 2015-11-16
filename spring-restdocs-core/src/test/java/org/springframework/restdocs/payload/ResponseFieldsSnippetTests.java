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
 * Tests for {@link ResponseFieldsSnippet}.
 *
 * @author Andy Wilkinson
 */
public class ResponseFieldsSnippetTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Rule
	public final ExpectedSnippet snippet = new ExpectedSnippet();

	@Test
	public void mapResponseWithFields() throws IOException {
		this.snippet.expectResponseFields("map-response-with-fields").withContents(
				tableWithHeader("Path", "Type", "Description").row("id", "Number", "one")
						.row("date", "String", "two").row("assets", "Array", "three")
						.row("assets[]", "Object", "four")
						.row("assets[].id", "Number", "five")
						.row("assets[].name", "String", "six"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("id").description("one"),
				fieldWithPath("date").description("two"), fieldWithPath("assets")
						.description("three"),
				fieldWithPath("assets[]").description("four"),
				fieldWithPath("assets[].id").description("five"),
				fieldWithPath("assets[].name").description("six")))
				.document(new OperationBuilder("map-response-with-fields", this.snippet
						.getOutputDirectory())
						.response()
						.content(
								"{\"id\": 67,\"date\": \"2015-01-20\",\"assets\":"
										+ " [{\"id\":356,\"name\": \"sample\"}]}")
						.build());
	}

	@Test
	public void arrayResponseWithFields() throws IOException {
		this.snippet.expectResponseFields("array-response-with-fields").withContents(
				tableWithHeader("Path", "Type", "Description")
						.row("[]a.b", "Number", "one").row("[]a.c", "String", "two")
						.row("[]a", "Object", "three"));
		new ResponseFieldsSnippet(Arrays.asList(
				fieldWithPath("[]a.b").description("one"), fieldWithPath("[]a.c")
						.description("two"), fieldWithPath("[]a").description("three")))
				.document(new OperationBuilder("array-response-with-fields", this.snippet
						.getOutputDirectory()).response()
						.content("[{\"a\": {\"b\": 5}},{\"a\": {\"c\": \"charlie\"}}]")
						.build());
	}

	@Test
	public void arrayResponse() throws IOException {
		this.snippet.expectResponseFields("array-response")
				.withContents(
						tableWithHeader("Path", "Type", "Description").row("[]",
								"String", "one"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("[]").description("one")))
				.document(new OperationBuilder("array-response", this.snippet
						.getOutputDirectory()).response()
						.content("[\"a\", \"b\", \"c\"]").build());
	}

	@Test
	public void ignoredResponseField() throws IOException {
		this.snippet.expectResponseFields("ignored-response-field").withContents(
				tableWithHeader("Path", "Type", "Description").row("b", "Number",
						"Field b"));

		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").ignored(),
				fieldWithPath("b").description("Field b")))
				.document(new OperationBuilder("ignored-response-field", this.snippet
						.getOutputDirectory()).response().content("{\"a\": 5, \"b\": 4}")
						.build());
	}

	@Test
	public void responseFieldsWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-fields")).willReturn(
				snippetResource("response-fields-with-extra-column"));
		this.snippet.expectResponseFields("response-fields-with-custom-attributes")
				.withContents(
						tableWithHeader("Path", "Type", "Description", "Foo")
								.row("a.b", "Number", "one", "alpha")
								.row("a.c", "String", "two", "bravo")
								.row("a", "Object", "three", "charlie"));

		new ResponseFieldsSnippet(Arrays.asList(
				fieldWithPath("a.b").description("one").attributes(
						key("foo").value("alpha")),
				fieldWithPath("a.c").description("two").attributes(
						key("foo").value("bravo")),
				fieldWithPath("a").description("three").attributes(
						key("foo").value("charlie")))).document(new OperationBuilder(
				"response-fields-with-custom-attributes", this.snippet
						.getOutputDirectory())
				.attribute(TemplateEngine.class.getName(),
						new MustacheTemplateEngine(resolver)).response()
				.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}").build());
	}

	@Test
	public void responseFieldsWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-fields")).willReturn(
				snippetResource("response-fields-with-title"));
		this.snippet.expectResponseFields("response-fields-with-custom-attributes")
				.withContents(startsWith(".Custom title"));

		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")),
				attributes(key("title").value("Custom title")))
				.document(new OperationBuilder("response-fields-with-custom-attributes",
						this.snippet.getOutputDirectory())
						.attribute(TemplateEngine.class.getName(),
								new MustacheTemplateEngine(resolver)).response()
						.content("{\"a\": \"foo\"}").build());
	}

	@Test
	public void xmlResponseFields() throws IOException {
		this.snippet.expectResponseFields("xml-response").withContents(
				tableWithHeader("Path", "Type", "Description").row("a/b", "b", "one")
						.row("a/c", "c", "two").row("a", "a", "three"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one")
				.type("b"), fieldWithPath("a/c").description("two").type("c"),
				fieldWithPath("a").description("three").type("a")))
				.document(new OperationBuilder("xml-response", this.snippet
						.getOutputDirectory())
						.response()
						.content("<a><b>5</b><c>charlie</c></a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	@Test
	public void undocumentedXmlResponseField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(startsWith("The following parts of the payload were not"
						+ " documented:"));
		new ResponseFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(new OperationBuilder("undocumented-xml-response-field",
						this.snippet.getOutputDirectory())
						.response()
						.content("<a><b>5</b></a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	@Test
	public void xmlAttribute() throws IOException {
		this.snippet.expectResponseFields("xml-attribute").withContents(
				tableWithHeader("Path", "Type", "Description").row("a", "b", "one").row(
						"a/@id", "c", "two"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")
				.type("b"), fieldWithPath("a/@id").description("two").type("c")))
				.document(new OperationBuilder("xml-attribute", this.snippet
						.getOutputDirectory())
						.response()
						.content("<a id=\"1\">foo</a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	@Test
	public void missingXmlAttribute() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Fields with the following paths were not found"
						+ " in the payload: [a/@id]"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")
				.type("b"), fieldWithPath("a/@id").description("two").type("c")))
				.document(new OperationBuilder("missing-xml-attribute", this.snippet
						.getOutputDirectory())
						.response()
						.content("<a>foo</a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	@Test
	public void missingOptionalXmlAttribute() throws IOException {
		this.snippet.expectResponseFields("missing-optional-xml-attribute").withContents(
				tableWithHeader("Path", "Type", "Description").row("a", "b", "one").row(
						"a/@id", "c", "two"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")
				.type("b"), fieldWithPath("a/@id").description("two").type("c")
				.optional())).document(new OperationBuilder(
				"missing-optional-xml-attribute", this.snippet.getOutputDirectory())
				.response().content("<a>foo</a>")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.build());
	}

	@Test
	public void undocumentedAttributeDoesNotCauseFailure() throws IOException {
		this.snippet.expectResponseFields("undocumented-attribute").withContents(
				tableWithHeader("Path", "Type", "Description").row("a", "a", "one"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")
				.type("a"))).document(new OperationBuilder("undocumented-attribute",
				this.snippet.getOutputDirectory()).response()
				.content("<a id=\"foo\">bar</a>")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.build());
	}

	@Test
	public void documentedXmlAttributesAreRemoved() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo(String
				.format("The following parts of the payload were not documented:"
						+ "%n<a>bar</a>%n")));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a/@id").description("one")
				.type("a"))).document(new OperationBuilder(
				"documented-attribute-is-removed", this.snippet.getOutputDirectory())
				.response().content("<a id=\"foo\">bar</a>")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.build());
	}

	@Test
	public void xmlResponseFieldWithNoType() throws IOException {
		this.thrown.expect(FieldTypeRequiredException.class);
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
				.document(new OperationBuilder("xml-response-no-field-type", this.snippet
						.getOutputDirectory())
						.response()
						.content("<a>5</a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	@Test
	public void missingXmlResponseField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Fields with the following paths were not found"
						+ " in the payload: [a/b]"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one"),
				fieldWithPath("a").description("one"))).document(new OperationBuilder(
				"missing-xml-response-field", this.snippet.getOutputDirectory())
				.response().content("<a></a>")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.build());
	}

	@Test
	public void undocumentedXmlResponseFieldAndMissingXmlResponseField()
			throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(startsWith("The following parts of the payload were not"
						+ " documented:"));
		this.thrown
				.expectMessage(endsWith("Fields with the following paths were not found"
						+ " in the payload: [a/b]"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one")))
				.document(new OperationBuilder(
						"undocumented-xml-request-field-and-missing-xml-request-field",
						this.snippet.getOutputDirectory())
						.response()
						.content("<a><c>5</c></a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	private FileSystemResource snippetResource(String name) {
		return new FileSystemResource("src/test/resources/custom-snippet-templates/"
				+ name + ".snippet");
	}

}
