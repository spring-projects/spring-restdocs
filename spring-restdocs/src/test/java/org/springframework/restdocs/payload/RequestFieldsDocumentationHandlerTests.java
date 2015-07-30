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

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;
import static org.springframework.restdocs.test.StubMvcResult.result;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;

/**
 * Tests for {@link RequestFieldsSnippet}
 * 
 * @author Andy Wilkinson
 */
public class RequestFieldsDocumentationHandlerTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Rule
	public final ExpectedSnippet snippet = new ExpectedSnippet();

	@Test
	public void mapRequestWithFields() throws IOException {
		this.snippet.expectRequestFields("map-request-with-fields").withContents( //
				tableWithHeader("Path", "Type", "Description") //
						.row("a.b", "Number", "one") //
						.row("a.c", "String", "two") //
						.row("a", "Object", "three"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b")
				.description("one"), fieldWithPath("a.c").description("two"),
				fieldWithPath("a").description("three"))).document(
				"map-request-with-fields",
				result(get("/foo").content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")));
	}

	@Test
	public void arrayRequestWithFields() throws IOException {
		this.snippet.expectRequestFields("array-request-with-fields").withContents( //
				tableWithHeader("Path", "Type", "Description") //
						.row("[]a.b", "Number", "one") //
						.row("[]a.c", "String", "two") //
						.row("[]a", "Object", "three"));

		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("[]a.b")
				.description("one"), fieldWithPath("[]a.c").description("two"),
				fieldWithPath("[]a").description("three"))).document(
				"array-request-with-fields",
				result(get("/foo").content(
						"[{\"a\": {\"b\": 5}},{\"a\": {\"c\": \"charlie\"}}]")));
	}

	@Test
	public void undocumentedRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(startsWith("The following parts of the payload were not"
						+ " documented:"));
		new RequestFieldsSnippet(Collections.<FieldDescriptor> emptyList())
				.document("undocumented-request-field",
						result(get("/foo").content("{\"a\": 5}")));
	}

	@Test
	public void missingRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Fields with the following paths were not found"
						+ " in the payload: [a.b]"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b")
				.description("one"))).document("missing-request-fields",
				result(get("/foo").content("{}")));
	}

	@Test
	public void missingOptionalRequestFieldWithNoTypeProvided() throws IOException {
		this.thrown.expect(FieldTypeRequiredException.class);
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b")
				.description("one").optional())).document(
				"missing-optional-request-field-with-no-type", result(get("/foo")
						.content("{ }")));
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
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b")
				.description("one"))).document(
				"undocumented-request-field-and-missing-request-field",
				result(get("/foo").content("{ \"a\": { \"c\": 5 }}")));
	}

	@Test
	public void requestFieldsWithCustomDescriptorAttributes() throws IOException {
		this.snippet.expectRequestFields(
				"request-fields-with-custom-descriptor-attributes").withContents( //
				tableWithHeader("Path", "Type", "Description", "Foo") //
						.row("a.b", "Number", "one", "alpha") //
						.row("a.c", "String", "two", "bravo") //
						.row("a", "Object", "three", "charlie"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("request-fields")).thenReturn(
				snippetResource("request-fields-with-extra-column"));
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(
				resolver));
		request.setContent("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}".getBytes());
		new RequestFieldsSnippet(Arrays.asList(
				fieldWithPath("a.b").description("one").attributes(
						key("foo").value("alpha")),
				fieldWithPath("a.c").description("two").attributes(
						key("foo").value("bravo")),
				fieldWithPath("a").description("three").attributes(
						key("foo").value("charlie")))).document(
				"request-fields-with-custom-descriptor-attributes", result(request));
	}

	@Test
	public void requestFieldsWithCustomAttributes() throws IOException {
		this.snippet.expectRequestFields("request-fields-with-custom-attributes")
				.withContents(startsWith(".Custom title"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("request-fields")).thenReturn(
				snippetResource("request-fields-with-title"));
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(
				resolver));
		request.setContent("{\"a\": \"foo\"}".getBytes());
		MockHttpServletResponse response = new MockHttpServletResponse();
		new RequestFieldsSnippet(attributes(key("title").value(
				"Custom title")), Arrays.asList(fieldWithPath("a").description("one")))
				.document("request-fields-with-custom-attributes",
						result(request, response));
	}

	private FileSystemResource snippetResource(String name) {
		return new FileSystemResource("src/test/resources/custom-snippet-templates/"
				+ name + ".snippet");
	}

}
