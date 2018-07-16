/*
 * Copyright 2014-2018 the original author or authors.
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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
 */
public class RequestFieldsSnippetTests extends AbstractSnippetTests {

	public RequestFieldsSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void mapRequestWithFields() throws IOException {
		this.snippets.expectRequestFields()
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
	public void mapRequestWithNullField() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`a.b`",
						"`Null`", "one"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")))
				.document(this.operationBuilder.request("http://localhost")
						.content("{\"a\": {\"b\": null}}").build());
	}

	@Test
	public void entireSubsectionsCanBeDocumented() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`a`",
						"`Object`", "one"));

		new RequestFieldsSnippet(
				Arrays.asList(subsectionWithPath("a").description("one")))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
								.build());
	}

	@Test
	public void subsectionOfMapRequest() throws IOException {
		this.snippets.expect("request-fields-beneath-a")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`b`", "`Number`", "one").row("`c`", "`String`", "two"));

		requestFields(beneathPath("a"), fieldWithPath("b").description("one"),
				fieldWithPath("c").description("two"))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
								.build());
	}

	@Test
	public void subsectionOfMapRequestWithCommonPrefix() throws IOException {
		this.snippets.expect("request-fields-beneath-a")
				.withContents(tableWithHeader("Path", "Type", "Description").row("`b.c`",
						"`String`", "two"));

		requestFields(beneathPath("a"))
				.andWithPrefix("b.", fieldWithPath("c").description("two"))
				.document(this.operationBuilder.request("http://localhost")
						.content("{\"a\": {\"b\": {\"c\": \"charlie\"}}}").build());
	}

	@Test
	public void arrayRequestWithFields() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`[]`", "`Array`", "one").row("`[]a.b`", "`Number`", "two")
						.row("`[]a.c`", "`String`", "three")
						.row("`[]a`", "`Object`", "four"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("[]").description("one"),
				fieldWithPath("[]a.b").description("two"),
				fieldWithPath("[]a.c").description("three"),
				fieldWithPath("[]a").description("four")))
						.document(this.operationBuilder.request("http://localhost")
								.content("[{\"a\": {\"b\": 5, \"c\":\"charlie\"}},"
										+ "{\"a\": {\"b\": 4, \"c\":\"chalk\"}}]")
								.build());
	}

	@Test
	public void arrayRequestWithAlwaysNullField() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`[]a.b`", "`Null`", "one"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("[]a.b").description("one")))
				.document(this.operationBuilder.request("http://localhost")
						.content("[{\"a\": {\"b\": null}}," + "{\"a\": {\"b\": null}}]")
						.build());
	}

	@Test
	public void subsectionOfArrayRequest() throws IOException {
		this.snippets.expect("request-fields-beneath-[].a")
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`b`", "`Number`", "one").row("`c`", "`String`", "two"));

		requestFields(beneathPath("[].a"), fieldWithPath("b").description("one"),
				fieldWithPath("c").description("two"))
						.document(this.operationBuilder.request("http://localhost")
								.content("[{\"a\": {\"b\": 5, \"c\": \"charlie\"}}]")
								.build());
	}

	@Test
	public void ignoredRequestField() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`b`",
						"`Number`", "Field b"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").ignored(),
				fieldWithPath("b").description("Field b")))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\": 5, \"b\": 4}").build());
	}

	@Test
	public void entireSubsectionCanBeIgnored() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`c`",
						"`Number`", "Field c"));

		new RequestFieldsSnippet(Arrays.asList(subsectionWithPath("a").ignored(),
				fieldWithPath("c").description("Field c")))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\": {\"b\": 5}, \"c\": 4}").build());
	}

	@Test
	public void allUndocumentedRequestFieldsCanBeIgnored() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`b`",
						"`Number`", "Field b"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("b").description("Field b")),
				true).document(
						this.operationBuilder.request("http://localhost")
								.content("{\"a\": 5, \"b\": 4}").build());
	}

	@Test
	public void allUndocumentedFieldsContinueToBeIgnoredAfterAddingDescriptors()
			throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`b`", "`Number`", "Field b")
						.row("`c.d`", "`Number`", "Field d"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("b").description("Field b")),
				true).andWithPrefix("c.", fieldWithPath("d").description("Field d"))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\":5,\"b\":4,\"c\":{\"d\": 3}}").build());
	}

	@Test
	public void missingOptionalRequestField() throws IOException {
		this.snippets.expectRequestFields()
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
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description"));
		new RequestFieldsSnippet(Arrays
				.asList(fieldWithPath("a.b").description("one").ignored().optional()))
						.document(this.operationBuilder.request("http://localhost")
								.content("{}").build());
	}

	@Test
	public void presentOptionalRequestField() throws IOException {
		this.snippets.expectRequestFields()
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
		this.snippets.expectRequestFields().withContents(containsString("Custom title"));

		new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("a").description("one")), attributes(
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
		this.snippets.expectRequestFields()
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
								.document(this.operationBuilder
										.attribute(TemplateEngine.class.getName(),
												new MustacheTemplateEngine(resolver))
										.request("http://localhost")
										.content(
												"{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
										.build());
	}

	@Test
	public void fieldWithExplictExactlyMatchingType() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`a`",
						"`Number`", "one"));

		new RequestFieldsSnippet(Arrays
				.asList(fieldWithPath("a").description("one").type(JsonFieldType.NUMBER)))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\": 5 }").build());
	}

	@Test
	public void fieldWithExplictVariesType() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`a`",
						"`Varies`", "one"));

		new RequestFieldsSnippet(Arrays
				.asList(fieldWithPath("a").description("one").type(JsonFieldType.VARIES)))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\": 5 }").build());
	}

	@Test
	public void applicationXmlRequestFields() throws IOException {
		xmlRequestFields(MediaType.APPLICATION_XML);
	}

	@Test
	public void textXmlRequestFields() throws IOException {
		xmlRequestFields(MediaType.TEXT_XML);
	}

	@Test
	public void customXmlRequestFields() throws IOException {
		xmlRequestFields(MediaType.parseMediaType("application/vnd.com.example+xml"));
	}

	private void xmlRequestFields(MediaType contentType) throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`a/b`", "`b`", "one").row("`a/c`", "`c`", "two")
						.row("`a`", "`a`", "three"));

		new RequestFieldsSnippet(Arrays.asList(
				fieldWithPath("a/b").description("one").type("b"),
				fieldWithPath("a/c").description("two").type("c"),
				fieldWithPath("a").description("three").type("a")))
						.document(this.operationBuilder.request("http://localhost")
								.content("<a><b>5</b><c>charlie</c></a>")
								.header(HttpHeaders.CONTENT_TYPE, contentType.toString())
								.build());
	}

	@Test
	public void entireSubsectionOfXmlPayloadCanBeDocumented() throws IOException {
		this.snippets.expectRequestFields().withContents(
				tableWithHeader("Path", "Type", "Description").row("`a`", "`a`", "one"));

		new RequestFieldsSnippet(
				Arrays.asList(subsectionWithPath("a").description("one").type("a")))
						.document(this.operationBuilder.request("http://localhost")
								.content("<a><b>5</b><c>charlie</c></a>")
								.header(HttpHeaders.CONTENT_TYPE,
										MediaType.APPLICATION_XML_VALUE)
								.build());
	}

	@Test
	public void additionalDescriptors() throws IOException {
		this.snippets.expectRequestFields()
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
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`a`", "`Object`", "one").row("`a.b`", "`Number`", "two")
						.row("`a.c`", "`String`", "three"));

		PayloadDocumentation.requestFields(fieldWithPath("a").description("one"))
				.andWithPrefix("a.", fieldWithPath("b").description("two"),
						fieldWithPath("c").description("three"))
				.document(this.operationBuilder.request("http://localhost")
						.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}").build());
	}

	@Test
	public void requestWithFieldsWithEscapedContent() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row(
						escapeIfNecessary("`Foo|Bar`"), escapeIfNecessary("`one|two`"),
						escapeIfNecessary("three|four")));

		new RequestFieldsSnippet(Arrays.asList(
				fieldWithPath("Foo|Bar").type("one|two").description("three|four")))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"Foo|Bar\": 5}").build());
	}

	@Test
	public void mapRequestWithVaryingKeysMatchedUsingWildcard() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`things.*.size`", "`String`", "one")
						.row("`things.*.type`", "`String`", "two"));

		new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("things.*.size").description("one"),
						fieldWithPath("things.*.type").description("two"))).document(
								this.operationBuilder.request("http://localhost")
										.content("{\"things\": {\"12abf\": {\"type\":"
												+ "\"Whale\", \"size\": \"HUGE\"},"
												+ "\"gzM33\" : {\"type\": \"Screw\","
												+ "\"size\": \"SMALL\"}}}")
										.build());
	}

	@Test
	public void requestWithArrayContainingFieldThatIsSometimesNull() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`assets[].name`", "`String`", "one"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("assets[].name")
				.description("one").type(JsonFieldType.STRING).optional()))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"assets\": [" + "{\"name\": \"sample1\"}, "
										+ "{\"name\": null}, "
										+ "{\"name\": \"sample2\"}]}")
								.build());
	}

	@Test
	public void optionalFieldBeneathArrayThatIsSometimesAbsent() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description")
						.row("`a[].b`", "`Number`", "one")
						.row("`a[].c`", "`Number`", "two"));
		new RequestFieldsSnippet(Arrays.asList(
				fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER)
						.optional(),
				fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
						.document(
								this.operationBuilder.request("http://localhost")
										.content("{\"a\":[{\"b\": 1,\"c\": 2}, "
												+ "{\"c\": 2}, {\"b\": 1,\"c\": 2}]}")
										.build());
	}

	@Test
	public void typeDeterminationDoesNotSetTypeOnDescriptor() throws IOException {
		this.snippets.expectRequestFields()
				.withContents(tableWithHeader("Path", "Type", "Description").row("`a.b`",
						"`Number`", "one"));
		FieldDescriptor descriptor = fieldWithPath("a.b").description("one");
		new RequestFieldsSnippet(Arrays.asList(descriptor)).document(this.operationBuilder
				.request("http://localhost").content("{\"a\": {\"b\": 5}}").build());
		assertThat(descriptor.getType(), is(nullValue()));
	}

	private String escapeIfNecessary(String input) {
		if (this.templateFormat.equals(TemplateFormats.markdown())) {
			return input;
		}
		return input.replace("|", "\\|");
	}

}
