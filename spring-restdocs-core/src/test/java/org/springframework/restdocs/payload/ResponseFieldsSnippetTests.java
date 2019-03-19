/*
 * Copyright 2014-2018 the original author or authors.
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

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("id").description("one"),
				fieldWithPath("date").description("two"),
				fieldWithPath("assets").description("three"),
				fieldWithPath("assets[]").description("four"),
				fieldWithPath("assets[].id").description("five"),
				fieldWithPath("assets[].name").description("six")))
						.document(this.operationBuilder.response()
								.content(
										"{\"id\": 67,\"date\": \"2015-01-20\",\"assets\":"
												+ " [{\"id\":356,\"name\": \"sample\"}]}")
								.build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`id`", "`Number`", "one").row("`date`", "`String`", "two")
						.row("`assets`", "`Array`", "three")
						.row("`assets[]`", "`Array`", "four")
						.row("`assets[].id`", "`Number`", "five")
						.row("`assets[].name`", "`String`", "six"));
	}

	@Test
	public void mapResponseWithNullField() throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")))
				.document(this.operationBuilder.response()
						.content("{\"a\": {\"b\": null}}").build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description").row("`a.b`", "`Null`",
						"one"));
	}

	@Test
	public void subsectionOfMapResponse() throws IOException {
		responseFields(beneathPath("a"), fieldWithPath("b").description("one"),
				fieldWithPath("c").description("two"))
						.document(this.operationBuilder.response()
								.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
								.build());
		assertThat(this.generatedSnippets.snippet("response-fields-beneath-a"))
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`b`", "`Number`", "one").row("`c`", "`String`", "two"));
	}

	@Test
	public void subsectionOfMapResponseBeneathAnArray() throws IOException {
		responseFields(beneathPath("a.b.[]"), fieldWithPath("c").description("one"),
				fieldWithPath("d.[].e").description("two"))
						.document(this.operationBuilder.response().content(
								"{\"a\": {\"b\": [{\"c\": 1, \"d\": [{\"e\": 5}]}, {\"c\": 3, \"d\": [{\"e\": 4}]}]}}")
								.build());
		assertThat(this.generatedSnippets.snippet("response-fields-beneath-a.b.[]"))
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`c`", "`Number`", "one")
						.row("`d.[].e`", "`Number`", "two"));
	}

	@Test
	public void subsectionOfMapResponseWithCommonsPrefix() throws IOException {
		responseFields(beneathPath("a"))
				.andWithPrefix("b.", fieldWithPath("c").description("two"))
				.document(this.operationBuilder.response()
						.content("{\"a\": {\"b\": {\"c\": \"charlie\"}}}").build());
		assertThat(this.generatedSnippets.snippet("response-fields-beneath-a"))
				.is(tableWithHeader("Path", "Type", "Description").row("`b.c`",
						"`String`", "two"));
	}

	@Test
	public void arrayResponseWithFields() throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("[]a.b").description("one"),
				fieldWithPath("[]a.c").description("two"),
				fieldWithPath("[]a").description("three")))
						.document(this.operationBuilder.response()
								.content("[{\"a\": {\"b\": 5, \"c\":\"charlie\"}},"
										+ "{\"a\": {\"b\": 4, \"c\":\"chalk\"}}]")
								.build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`[]a.b`", "`Number`", "one")
						.row("`[]a.c`", "`String`", "two")
						.row("`[]a`", "`Object`", "three"));
	}

	@Test
	public void arrayResponseWithAlwaysNullField() throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("[]a.b").description("one")))
						.document(this.operationBuilder.response().content(
								"[{\"a\": {\"b\": null}}," + "{\"a\": {\"b\": null}}]")
								.build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description").row("`[]a.b`",
						"`Null`", "one"));
	}

	@Test
	public void arrayResponse() throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("[]").description("one")))
				.document(this.operationBuilder.response()
						.content("[\"a\", \"b\", \"c\"]").build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description").row("`[]`", "`Array`",
						"one"));
	}

	@Test
	public void ignoredResponseField() throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").ignored(),
				fieldWithPath("b").description("Field b")))
						.document(this.operationBuilder.response()
								.content("{\"a\": 5, \"b\": 4}").build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description").row("`b`", "`Number`",
						"Field b"));
	}

	@Test
	public void allUndocumentedFieldsCanBeIgnored() throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("b").description("Field b")), true)
						.document(this.operationBuilder.response()
								.content("{\"a\": 5, \"b\": 4}").build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description").row("`b`", "`Number`",
						"Field b"));
	}

	@Test
	public void allUndocumentedFieldsContinueToBeIgnoredAfterAddingDescriptors()
			throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("b").description("Field b")), true)
						.andWithPrefix("c.", fieldWithPath("d").description("Field d"))
						.document(this.operationBuilder.response()
								.content("{\"a\":5,\"b\":4,\"c\":{\"d\": 3}}").build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`b`", "`Number`", "Field b")
						.row("`c.d`", "`Number`", "Field d"));
	}

	@Test
	public void responseFieldsWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-fields"))
				.willReturn(snippetResource("response-fields-with-title"));
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a").description("one")), attributes(
						key("title").value("Custom title")))
								.document(
										this.operationBuilder
												.attribute(TemplateEngine.class.getName(),
														new MustacheTemplateEngine(
																resolver))
												.response().content("{\"a\": \"foo\"}")
												.build());
		assertThat(this.generatedSnippets.responseFields()).contains("Custom title");
	}

	@Test
	public void missingOptionalResponseField() throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")
				.type(JsonFieldType.STRING).optional()))
						.document(this.operationBuilder.response().content("{}").build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description").row("`a.b`",
						"`String`", "one"));
	}

	@Test
	public void missingIgnoredOptionalResponseFieldDoesNotRequireAType()
			throws IOException {
		new ResponseFieldsSnippet(Arrays
				.asList(fieldWithPath("a.b").description("one").ignored().optional()))
						.document(this.operationBuilder.response().content("{}").build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description"));
	}

	@Test
	public void presentOptionalResponseField() throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")
				.type(JsonFieldType.STRING).optional()))
						.document(this.operationBuilder.response()
								.content("{\"a\": { \"b\": \"bravo\"}}").build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description").row("`a.b`",
						"`String`", "one"));
	}

	@Test
	public void responseFieldsWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-fields"))
				.willReturn(snippetResource("response-fields-with-extra-column"));
		new ResponseFieldsSnippet(Arrays.asList(
				fieldWithPath("a.b").description("one")
						.attributes(key("foo").value("alpha")),
				fieldWithPath("a.c").description("two")
						.attributes(key("foo").value("bravo")),
				fieldWithPath("a").description("three")
						.attributes(key("foo").value("charlie"))))
								.document(this.operationBuilder
										.attribute(TemplateEngine.class.getName(),
												new MustacheTemplateEngine(resolver))
										.response()
										.content(
												"{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")
										.build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description", "Foo")
						.row("a.b", "Number", "one", "alpha")
						.row("a.c", "String", "two", "bravo")
						.row("a", "Object", "three", "charlie"));
	}

	@Test
	public void fieldWithExplictExactlyMatchingType() throws IOException {
		new ResponseFieldsSnippet(Arrays
				.asList(fieldWithPath("a").description("one").type(JsonFieldType.NUMBER)))
						.document(this.operationBuilder.response().content("{\"a\": 5 }")
								.build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description").row("`a`", "`Number`",
						"one"));
	}

	@Test
	public void fieldWithExplictVariesType() throws IOException {
		new ResponseFieldsSnippet(Arrays
				.asList(fieldWithPath("a").description("one").type(JsonFieldType.VARIES)))
						.document(this.operationBuilder.response().content("{\"a\": 5 }")
								.build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description").row("`a`", "`Varies`",
						"one"));
	}

	@Test
	public void applicationXmlResponseFields() throws IOException {
		xmlResponseFields(MediaType.APPLICATION_XML);
	}

	@Test
	public void textXmlResponseFields() throws IOException {
		xmlResponseFields(MediaType.TEXT_XML);
	}

	@Test
	public void customXmlResponseFields() throws IOException {
		xmlResponseFields(MediaType.parseMediaType("application/vnd.com.example+xml"));
	}

	private void xmlResponseFields(MediaType contentType) throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(
				fieldWithPath("a/b").description("one").type("b"),
				fieldWithPath("a/c").description("two").type("c"),
				fieldWithPath("a").description("three").type("a")))
						.document(this.operationBuilder.response()
								.content("<a><b>5</b><c>charlie</c></a>")
								.header(HttpHeaders.CONTENT_TYPE, contentType.toString())
								.build());
		assertThat(this.generatedSnippets.responseFields()).is(
				tableWithHeader("Path", "Type", "Description").row("`a/b`", "`b`", "one")
						.row("`a/c`", "`c`", "two").row("`a`", "`a`", "three"));
	}

	@Test
	public void xmlAttribute() throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a").description("one").type("b"),
						fieldWithPath("a/@id").description("two").type("c")))
								.document(
										this.operationBuilder.response()
												.content("<a id=\"1\">foo</a>")
												.header(HttpHeaders.CONTENT_TYPE,
														MediaType.APPLICATION_XML_VALUE)
												.build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`a`", "`b`", "one").row("`a/@id`", "`c`", "two"));
	}

	@Test
	public void missingOptionalXmlAttribute() throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a").description("one").type("b"),
						fieldWithPath("a/@id").description("two").type("c").optional()))
								.document(
										this.operationBuilder.response()
												.content("<a>foo</a>")
												.header(HttpHeaders.CONTENT_TYPE,
														MediaType.APPLICATION_XML_VALUE)
												.build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`a`", "`b`", "one").row("`a/@id`", "`c`", "two"));
	}

	@Test
	public void undocumentedAttributeDoesNotCauseFailure() throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a").description("one").type("a"))).document(
						this.operationBuilder.response().content("<a id=\"foo\">bar</a>")
								.header(HttpHeaders.CONTENT_TYPE,
										MediaType.APPLICATION_XML_VALUE)
								.build());
		assertThat(this.generatedSnippets.responseFields()).is(
				tableWithHeader("Path", "Type", "Description").row("`a`", "`a`", "one"));
	}

	@Test
	public void additionalDescriptors() throws IOException {
		PayloadDocumentation
				.responseFields(fieldWithPath("id").description("one"),
						fieldWithPath("date").description("two"),
						fieldWithPath("assets").description("three"))
				.and(fieldWithPath("assets[]").description("four"),
						fieldWithPath("assets[].id").description("five"),
						fieldWithPath("assets[].name").description("six"))
				.document(this.operationBuilder.response()
						.content("{\"id\": 67,\"date\": \"2015-01-20\",\"assets\":"
								+ " [{\"id\":356,\"name\": \"sample\"}]}")
						.build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`id`", "`Number`", "one").row("`date`", "`String`", "two")
						.row("`assets`", "`Array`", "three")
						.row("`assets[]`", "`Array`", "four")
						.row("`assets[].id`", "`Number`", "five")
						.row("`assets[].name`", "`String`", "six"));
	}

	@Test
	public void prefixedAdditionalDescriptors() throws IOException {
		PayloadDocumentation.responseFields(fieldWithPath("a").description("one"))
				.andWithPrefix("a.", fieldWithPath("b").description("two"),
						fieldWithPath("c").description("three"))
				.document(this.operationBuilder.response()
						.content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}").build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`a`", "`Object`", "one").row("`a.b`", "`Number`", "two")
						.row("`a.c`", "`String`", "three"));
	}

	@Test
	public void responseWithFieldsWithEscapedContent() throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(
				fieldWithPath("Foo|Bar").type("one|two").description("three|four")))
						.document(this.operationBuilder.response()
								.content("{\"Foo|Bar\": 5}").build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description").row(
						escapeIfNecessary("`Foo|Bar`"), escapeIfNecessary("`one|two`"),
						escapeIfNecessary("three|four")));
	}

	@Test
	public void mapResponseWithVaryingKeysMatchedUsingWildcard() throws IOException {
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("things.*.size").description("one"),
						fieldWithPath("things.*.type").description("two")))
								.document(this.operationBuilder.response()
										.content("{\"things\": {\"12abf\": {\"type\":"
												+ "\"Whale\", \"size\": \"HUGE\"},"
												+ "\"gzM33\" : {\"type\": \"Screw\","
												+ "\"size\": \"SMALL\"}}}")
										.build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`things.*.size`", "`String`", "one")
						.row("`things.*.type`", "`String`", "two"));
	}

	@Test
	public void responseWithArrayContainingFieldThatIsSometimesNull() throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("assets[].name")
				.description("one").type(JsonFieldType.STRING).optional()))
						.document(this.operationBuilder.response()
								.content("{\"assets\": [" + "{\"name\": \"sample1\"}, "
										+ "{\"name\": null}, "
										+ "{\"name\": \"sample2\"}]}")
								.build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description").row("`assets[].name`",
						"`String`", "one"));
	}

	@Test
	public void optionalFieldBeneathArrayThatIsSometimesAbsent() throws IOException {
		new ResponseFieldsSnippet(Arrays.asList(
				fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER)
						.optional(),
				fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
						.document(
								this.operationBuilder.response()
										.content("{\"a\":[{\"b\": 1,\"c\": 2}, "
												+ "{\"c\": 2}, {\"b\": 1,\"c\": 2}]}")
										.build());
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description")
						.row("`a[].b`", "`Number`", "one")
						.row("`a[].c`", "`Number`", "two"));
	}

	@Test
	public void typeDeterminationDoesNotSetTypeOnDescriptor() throws IOException {
		FieldDescriptor descriptor = fieldWithPath("id").description("one");
		new ResponseFieldsSnippet(Arrays.asList(descriptor)).document(
				this.operationBuilder.response().content("{\"id\": 67}").build());
		assertThat(descriptor.getType()).isNull();
		assertThat(this.generatedSnippets.responseFields())
				.is(tableWithHeader("Path", "Type", "Description").row("`id`", "`Number`",
						"one"));
	}

	private String escapeIfNecessary(String input) {
		if (this.templateFormat.getId().equals(TemplateFormats.markdown().getId())) {
			return input;
		}
		return input.replace("|", "\\|");
	}

}
