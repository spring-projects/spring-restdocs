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
import static org.springframework.restdocs.payload.PayloadDocumentation.documentRequestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.documentResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;
import static org.springframework.restdocs.test.StubMvcResult.result;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.snippet.SnippetGenerationException;
import org.springframework.restdocs.test.ExpectedSnippet;

/**
 * Tests for {@link PayloadDocumentation}
 * 
 * @author Andy Wilkinson
 */
public class PayloadDocumentationTests {

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

		documentRequestFields("map-request-with-fields",
				fieldWithPath("a.b").description("one"),
				fieldWithPath("a.c").description("two"),
				fieldWithPath("a").description("three")).handle(
				result(get("/foo").content("{\"a\": {\"b\": 5, \"c\": \"charlie\"}}")));
	}

	@Test
	public void arrayRequestWithFields() throws IOException {
		this.snippet.expectRequestFields("array-request-with-fields").withContents( //
				tableWithHeader("Path", "Type", "Description") //
						.row("[]a.b", "Number", "one") //
						.row("[]a.c", "String", "two") //
						.row("[]a", "Object", "three"));

		documentRequestFields("array-request-with-fields",
				fieldWithPath("[]a.b").description("one"),
				fieldWithPath("[]a.c").description("two"),
				fieldWithPath("[]a").description("three")).handle(
				result(get("/foo").content(
						"[{\"a\": {\"b\": 5}},{\"a\": {\"c\": \"charlie\"}}]")));
	}

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
		documentResponseFields("map-response-with-fields",
				fieldWithPath("id").description("one"),
				fieldWithPath("date").description("two"),
				fieldWithPath("assets").description("three"),
				fieldWithPath("assets[]").description("four"),
				fieldWithPath("assets[].id").description("five"),
				fieldWithPath("assets[].name").description("six")).handle(
				result(response));
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
		documentResponseFields("array-response-with-fields",
				fieldWithPath("[]a.b").description("one"),
				fieldWithPath("[]a.c").description("two"),
				fieldWithPath("[]a").description("three")).handle(result(response));
	}

	@Test
	public void arrayResponse() throws IOException {
		this.snippet.expectResponseFields("array-response").withContents( //
				tableWithHeader("Path", "Type", "Description") //
						.row("[]", "String", "one"));

		MockHttpServletResponse response = new MockHttpServletResponse();
		response.getWriter().append("[\"a\", \"b\", \"c\"]");
		documentResponseFields("array-response", fieldWithPath("[]").description("one"))
				.handle(result(response));
	}

	@Test
	public void undocumentedRequestField() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown
				.expectMessage(startsWith("The following parts of the payload were not"
						+ " documented:"));
		documentRequestFields("undocumented-request-fields").handle(
				result(get("/foo").content("{\"a\": 5}")));
	}

	@Test
	public void missingRequestField() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown
				.expectMessage(equalTo("Fields with the following paths were not found"
						+ " in the payload: [a.b]"));
		documentRequestFields("missing-request-fields",
				fieldWithPath("a.b").description("one")).handle(
				result(get("/foo").content("{}")));
	}

	@Test
	public void missingOptionalRequestFieldWithNoTypeProvided() throws IOException {
		this.thrown.expect(FieldTypeRequiredException.class);
		documentRequestFields("missing-optional-request-field-with-no-type",
				fieldWithPath("a.b").description("one").optional()).handle(
				result(get("/foo").content("{ }")));
	}

	@Test
	public void undocumentedRequestFieldAndMissingRequestField() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown
				.expectMessage(startsWith("The following parts of the payload were not"
						+ " documented:"));
		this.thrown
				.expectMessage(endsWith("Fields with the following paths were not found"
						+ " in the payload: [a.b]"));
		documentRequestFields("undocumented-request-field-and-missing-request-field",
				fieldWithPath("a.b").description("one")).handle(
				result(get("/foo").content("{ \"a\": { \"c\": 5 }}")));
	}
}
