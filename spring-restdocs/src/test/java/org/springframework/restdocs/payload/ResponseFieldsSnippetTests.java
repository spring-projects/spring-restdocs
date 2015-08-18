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

import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;
import static org.springframework.restdocs.test.StubMvcResult.result;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;

/**
 * Tests for {@link PayloadDocumentation}
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
		this.snippet.expectResponseFields("map-response-with-fields").withContents(//
				tableWithHeader("Path", "Type", "Description") //
						.row("id", "Number", "one") //
						.row("date", "String", "two") //
						.row("assets", "Array", "three") //
						.row("assets[]", "Object", "four") //
						.row("assets[].id", "Number", "five") //
						.row("assets[].name", "String", "six"));

		MockHttpServletResponse response = new MockHttpServletResponse();
		response.getWriter().append(
				"{\"id\": 67,\"date\": \"2015-01-20\",\"assets\":"
						+ " [{\"id\":356,\"name\": \"sample\"}]}");
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("id")
				.description("one"), fieldWithPath("date").description("two"),
				fieldWithPath("assets").description("three"), fieldWithPath("assets[]")
						.description("four"),
				fieldWithPath("assets[].id").description("five"),
				fieldWithPath("assets[].name").description("six"))).document(
				"map-response-with-fields", result(response));
	}

	@Test
	public void arrayResponseWithFields() throws IOException {
		this.snippet.expectResponseFields("array-response-with-fields").withContents( //
				tableWithHeader("Path", "Type", "Description") //
						.row("[]a.b", "Number", "one") //
						.row("[]a.c", "String", "two") //
						.row("[]a", "Object", "three"));

		MockHttpServletResponse response = new MockHttpServletResponse();
		response.getWriter()
				.append("[{\"a\": {\"b\": 5}},{\"a\": {\"c\": \"charlie\"}}]");
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("[]a.b")
				.description("one"), fieldWithPath("[]a.c").description("two"),
				fieldWithPath("[]a").description("three"))).document(
				"array-response-with-fields", result(response));
	}

	@Test
	public void arrayResponse() throws IOException {
		this.snippet.expectResponseFields("array-response").withContents( //
				tableWithHeader("Path", "Type", "Description") //
						.row("[]", "String", "one"));

		MockHttpServletResponse response = new MockHttpServletResponse();
		response.getWriter().append("[\"a\", \"b\", \"c\"]");
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("[]")
				.description("one"))).document("array-response", result(response));
	}

	@Test
	public void responseFieldsWithCustomDescriptorAttributes() throws IOException {
		this.snippet.expectResponseFields("response-fields-with-custom-attributes")
				.withContents( //
						tableWithHeader("Path", "Type", "Description", "Foo") //
								.row("a.b", "Number", "one", "alpha") //
								.row("a.c", "String", "two", "bravo") //
								.row("a", "Object", "three", "charlie"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("response-fields")).thenReturn(
				snippetResource("response-fields-with-extra-column"));
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(
				resolver));
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.getOutputStream().print("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}");
		new ResponseFieldsSnippet(Arrays.asList(
				fieldWithPath("a.b").description("one").attributes(
						key("foo").value("alpha")),
				fieldWithPath("a.c").description("two").attributes(
						key("foo").value("bravo")),
				fieldWithPath("a").description("three").attributes(
						key("foo").value("charlie")))).document(
				"response-fields-with-custom-attributes", result(request, response));
	}

	@Test
	public void responseFieldsWithCustomAttributes() throws IOException {
		this.snippet.expectResponseFields("response-fields-with-custom-attributes")
				.withContents(startsWith(".Custom title"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("response-fields")).thenReturn(
				snippetResource("response-fields-with-title"));
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(
				resolver));
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.getOutputStream().print("{\"a\": \"foo\"}");
		new ResponseFieldsSnippet(attributes(key("title").value(
				"Custom title")), Arrays.asList(fieldWithPath("a").description("one")))
				.document("response-fields-with-custom-attributes",
						result(request, response));
	}

	private FileSystemResource snippetResource(String name) {
		return new FileSystemResource("src/test/resources/custom-snippet-templates/"
				+ name + ".snippet");
	}

}
