/*
 * Copyright 2014-2023 the original author or authors.
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

import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.testfixtures.OperationBuilder;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Tests for failures when rendering {@link RequestFieldsSnippet} due to missing or
 * undocumented fields.
 *
 * @author Andy Wilkinson
 */
public class RequestFieldsSnippetFailureTests {

	@Rule
	public OperationBuilder operationBuilder = new OperationBuilder(TemplateFormats.asciidoctor());

	@Test
	public void undocumentedRequestField() {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(this.operationBuilder.request("http://localhost").content("{\"a\": 5}").build()))
			.withMessageStartingWith("The following parts of the payload were not documented:");
	}

	@Test
	public void missingRequestField() {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")))
				.document(this.operationBuilder.request("http://localhost").content("{}").build()))
			.withMessage("Fields with the following paths were not found in the payload: [a.b]");
	}

	@Test
	public void missingOptionalRequestFieldWithNoTypeProvided() {
		assertThatExceptionOfType(FieldTypeRequiredException.class).isThrownBy(
				() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one").optional()))
					.document(this.operationBuilder.request("http://localhost").content("{ }").build()));
	}

	@Test
	public void undocumentedRequestFieldAndMissingRequestField() {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a.b").description("one")))
				.document(this.operationBuilder.request("http://localhost").content("{ \"a\": { \"c\": 5 }}").build()))
			.withMessageStartingWith("The following parts of the payload were not documented:")
			.withMessageEndingWith("Fields with the following paths were not found in the payload: [a.b]");
	}

	@Test
	public void attemptToDocumentFieldsWithNoRequestBody() {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
				.document(this.operationBuilder.request("http://localhost").build()))
			.withMessage("Cannot document request fields as the request body is empty");
	}

	@Test
	public void fieldWithExplicitTypeThatDoesNotMatchThePayload() {
		assertThatExceptionOfType(FieldTypesDoNotMatchException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(
					Arrays.asList(fieldWithPath("a").description("one").type(JsonFieldType.OBJECT)))
				.document(this.operationBuilder.request("http://localhost").content("{ \"a\": 5 }").build()))
			.withMessage("The documented type of the field 'a' is Object but the actual type is Number");
	}

	@Test
	public void fieldWithExplicitSpecificTypeThatActuallyVaries() {
		assertThatExceptionOfType(FieldTypesDoNotMatchException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(
					Arrays.asList(fieldWithPath("[].a").description("one").type(JsonFieldType.OBJECT)))
				.document(this.operationBuilder.request("http://localhost")
					.content("[{ \"a\": 5 },{ \"a\": \"b\" }]")
					.build()))
			.withMessage("The documented type of the field '[].a' is Object but the actual type is Varies");
	}

	@Test
	public void undocumentedXmlRequestField() {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(this.operationBuilder.request("http://localhost")
					.content("<a><b>5</b></a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()))
			.withMessageStartingWith("The following parts of the payload were not documented:");
	}

	@Test
	public void xmlDescendentsAreNotDocumentedByFieldDescriptor() {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").type("a").description("one")))
				.document(this.operationBuilder.request("http://localhost")
					.content("<a><b>5</b></a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()))
			.withMessageStartingWith("The following parts of the payload were not documented:");
	}

	@Test
	public void xmlRequestFieldWithNoType() {
		assertThatExceptionOfType(FieldTypeRequiredException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
				.document(this.operationBuilder.request("http://localhost")
					.content("<a>5</a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()));
	}

	@Test
	public void missingXmlRequestField() {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(
					Arrays.asList(fieldWithPath("a/b").description("one"), fieldWithPath("a").description("one")))
				.document(this.operationBuilder.request("http://localhost")
					.content("<a></a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()))
			.withMessage("Fields with the following paths were not found in the payload: [a/b]");
	}

	@Test
	public void undocumentedXmlRequestFieldAndMissingXmlRequestField() {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one")))
				.document(this.operationBuilder.request("http://localhost")
					.content("<a><c>5</c></a>")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
					.build()))
			.withMessageStartingWith("The following parts of the payload were not documented:")
			.withMessageEndingWith("Fields with the following paths were not found in the payload: [a/b]");
	}

	@Test
	public void unsupportedContent() {
		assertThatExceptionOfType(PayloadHandlingException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(Collections.<FieldDescriptor>emptyList())
				.document(this.operationBuilder.request("http://localhost")
					.content("Some plain text")
					.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
					.build()))
			.withMessage("Cannot handle text/plain content as it could not be parsed as JSON or XML");
	}

	@Test
	public void nonOptionalFieldBeneathArrayThatIsSometimesNull() {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(
					Arrays.asList(fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER),
							fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
				.document(this.operationBuilder.request("http://localhost")
					.content("{\"a\":[{\"b\": 1,\"c\": 2}, " + "{\"b\": null, \"c\": 2}," + " {\"b\": 1,\"c\": 2}]}")
					.build()))
			.withMessageStartingWith("Fields with the following paths were not found in the payload: [a[].b]");
	}

	@Test
	public void nonOptionalFieldBeneathArrayThatIsSometimesAbsent() {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestFieldsSnippet(
					Arrays.asList(fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER),
							fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
				.document(this.operationBuilder.request("http://localhost")
					.content("{\"a\":[{\"b\": 1,\"c\": 2}, " + "{\"c\": 2}, {\"b\": 1,\"c\": 2}]}")
					.build()))
			.withMessageStartingWith("Fields with the following paths were not found in the payload: [a[].b]");
	}

}
