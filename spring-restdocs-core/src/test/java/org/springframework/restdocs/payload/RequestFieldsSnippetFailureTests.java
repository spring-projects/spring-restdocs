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
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.test.ExpectedSnippets;
import org.springframework.restdocs.test.OperationBuilder;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Tests for failures when rendering {@link RequestFieldsSnippet} due to missing or
 * undocumented fields.
 *
 * @author Andy Wilkinson
 */
public class RequestFieldsSnippetFailureTests {

	@Rule
	public ExpectedSnippets snippets = new ExpectedSnippets(
			TemplateFormats.asciidoctor());

	@Rule
	public OperationBuilder operationBuilder = new OperationBuilder(
			TemplateFormats.asciidoctor());

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void undocumentedRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(startsWith(
				"The following parts of the payload were not" + " documented:"));
		new RequestFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(this.operationBuilder.request("http://localhost")
						.content("{\"a\": 5}").build());
	}

	@Test
	public void missingRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Fields with the following paths were not found"
				+ " in the payload: [a.b]"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")))
				.document(this.operationBuilder.request("http://localhost").content("{}")
						.build());
	}

	@Test
	public void missingOptionalRequestFieldWithNoTypeProvided() throws IOException {
		this.thrown.expect(FieldTypeRequiredException.class);
		new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("a.b").description("one").optional()))
						.document(this.operationBuilder.request("http://localhost")
								.content("{ }").build());
	}

	@Test
	public void undocumentedRequestFieldAndMissingRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(startsWith(
				"The following parts of the payload were not" + " documented:"));
		this.thrown
				.expectMessage(endsWith("Fields with the following paths were not found"
						+ " in the payload: [a.b]"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")))
				.document(this.operationBuilder.request("http://localhost")
						.content("{ \"a\": { \"c\": 5 }}").build());
	}

	@Test
	public void attemptToDocumentFieldsWithNoRequestBody() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(
				equalTo("Cannot document request fields as the request body is empty"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
				.document(this.operationBuilder.request("http://localhost").build());
	}

	@Test
	public void fieldWithExplicitTypeThatDoesNotMatchThePayload() throws IOException {
		this.thrown.expect(FieldTypesDoNotMatchException.class);
		this.thrown.expectMessage(equalTo("The documented type of the field 'a' is"
				+ " Object but the actual type is Number"));
		new RequestFieldsSnippet(Arrays
				.asList(fieldWithPath("a").description("one").type(JsonFieldType.OBJECT)))
						.document(this.operationBuilder.request("http://localhost")
								.content("{ \"a\": 5 }").build());
	}

	@Test
	public void fieldWithExplicitSpecificTypeThatActuallyVaries() throws IOException {
		this.thrown.expect(FieldTypesDoNotMatchException.class);
		this.thrown.expectMessage(equalTo("The documented type of the field '[].a' is"
				+ " Object but the actual type is Varies"));
		new RequestFieldsSnippet(Arrays.asList(
				fieldWithPath("[].a").description("one").type(JsonFieldType.OBJECT)))
						.document(this.operationBuilder.request("http://localhost")
								.content("[{ \"a\": 5 },{ \"a\": \"b\" }]").build());
	}

	@Test
	public void undocumentedXmlRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(
				startsWith("The following parts of the payload were not documented:"));
		new RequestFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(this.operationBuilder.request("http://localhost")
						.content("<a><b>5</b></a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	@Test
	public void xmlDescendentsAreNotDocumentedByFieldDescriptor() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(
				startsWith("The following parts of the payload were not documented:"));
		new RequestFieldsSnippet(
				Arrays.asList(fieldWithPath("a").type("a").description("one")))
						.document(this.operationBuilder.request("http://localhost")
								.content("<a><b>5</b></a>")
								.header(HttpHeaders.CONTENT_TYPE,
										MediaType.APPLICATION_XML_VALUE)
								.build());
	}

	@Test
	public void xmlRequestFieldWithNoType() throws IOException {
		this.thrown.expect(FieldTypeRequiredException.class);
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
				.document(this.operationBuilder.request("http://localhost")
						.content("<a>5</a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	@Test
	public void missingXmlRequestField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Fields with the following paths were not found"
				+ " in the payload: [a/b]"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one"),
				fieldWithPath("a").description("one"))).document(this.operationBuilder
						.request("http://localhost").content("<a></a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	@Test
	public void undocumentedXmlRequestFieldAndMissingXmlRequestField()
			throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(
				startsWith("The following parts of the payload were not documented:"));
		this.thrown
				.expectMessage(endsWith("Fields with the following paths were not found"
						+ " in the payload: [a/b]"));
		new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one")))
				.document(this.operationBuilder.request("http://localhost")
						.content("<a><c>5</c></a>")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
						.build());
	}

	@Test
	public void unsupportedContent() throws IOException {
		this.thrown.expect(PayloadHandlingException.class);
		this.thrown.expectMessage(equalTo("Cannot handle text/plain content as it could"
				+ " not be parsed as JSON or XML"));
		new RequestFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(this.operationBuilder.request("http://localhost")
						.content("Some plain text")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
						.build());
	}

	@Test
	public void nonOptionalFieldBeneathArrayThatIsSometimesNull() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(startsWith(
				"Fields with the following paths were not found in the payload: "
						+ "[a[].b]"));
		new RequestFieldsSnippet(Arrays.asList(
				fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER),
				fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
						.document(this.operationBuilder.request("http://localhost")
								.content("{\"a\":[{\"b\": 1,\"c\": 2}, "
										+ "{\"b\": null, \"c\": 2},"
										+ " {\"b\": 1,\"c\": 2}]}")
								.build());
	}

	@Test
	public void nonOptionalFieldBeneathArrayThatIsSometimesAbsent() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(startsWith(
				"Fields with the following paths were not found in the payload: "
						+ "[a[].b]"));
		new RequestFieldsSnippet(Arrays.asList(
				fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER),
				fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
						.document(
								this.operationBuilder.request("http://localhost")
										.content("{\"a\":[{\"b\": 1,\"c\": 2}, "
												+ "{\"c\": 2}, {\"b\": 1,\"c\": 2}]}")
										.build());
	}

}
