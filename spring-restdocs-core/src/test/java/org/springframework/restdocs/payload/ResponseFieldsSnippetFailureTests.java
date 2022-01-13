/*
 * Copyright 2014-2022 the original author or authors.
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
 * Tests for failures when rendering {@link ResponseFieldsSnippet} due to missing or
 * undocumented fields.
 *
 * @author Andy Wilkinson
 */
public class ResponseFieldsSnippetFailureTests {

	@Rule
	public OperationBuilder operationBuilder = new OperationBuilder(TemplateFormats.asciidoctor());

	@Test
	public void attemptToDocumentFieldsWithNoResponseBody() throws IOException {
		assertThatExceptionOfType(SnippetException.class)
				.isThrownBy(() -> new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
						.document(this.operationBuilder.build()))
				.withMessage("Cannot document response fields as the response body is empty");
	}

	@Test
	public void fieldWithExplicitTypeThatDoesNotMatchThePayload() throws IOException {
		assertThatExceptionOfType(FieldTypesDoNotMatchException.class)
				.isThrownBy(() -> new ResponseFieldsSnippet(
						Arrays.asList(fieldWithPath("a").description("one").type(JsonFieldType.OBJECT)))
								.document(this.operationBuilder.response().content("{ \"a\": 5 }}").build()))
				.withMessage("The documented type of the field 'a' is Object but the actual type is Number");
	}

	@Test
	public void fieldWithExplicitSpecificTypeThatActuallyVaries() throws IOException {
		assertThatExceptionOfType(FieldTypesDoNotMatchException.class)
				.isThrownBy(() -> new ResponseFieldsSnippet(
						Arrays.asList(fieldWithPath("[].a").description("one").type(JsonFieldType.OBJECT))).document(
								this.operationBuilder.response().content("[{ \"a\": 5 },{ \"a\": \"b\" }]").build()))
				.withMessage("The documented type of the field '[].a' is Object but the actual type is Varies");
	}

	@Test
	public void undocumentedXmlResponseField() throws IOException {
		assertThatExceptionOfType(SnippetException.class)
				.isThrownBy(() -> new ResponseFieldsSnippet(Collections.<FieldDescriptor>emptyList())
						.document(this.operationBuilder.response().content("<a><b>5</b></a>")
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE).build()))
				.withMessageStartingWith("The following parts of the payload were not documented:");
	}

	@Test
	public void missingXmlAttribute() throws IOException {
		assertThatExceptionOfType(SnippetException.class)
				.isThrownBy(
						() -> new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one").type("b"),
								fieldWithPath("a/@id").description("two").type("c")))
										.document(
												this.operationBuilder.response().content("<a>foo</a>")
														.header(HttpHeaders.CONTENT_TYPE,
																MediaType.APPLICATION_XML_VALUE)
														.build()))
				.withMessage("Fields with the following paths were not found in the payload: [a/@id]");
	}

	@Test
	public void documentedXmlAttributesAreRemoved() throws IOException {
		assertThatExceptionOfType(SnippetException.class).isThrownBy(
				() -> new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a/@id").description("one").type("a")))
						.document(this.operationBuilder.response().content("<a id=\"foo\">bar</a>")
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE).build()))
				.withMessage(String.format("The following parts of the payload were not documented:%n<a>bar</a>%n"));
	}

	@Test
	public void xmlResponseFieldWithNoType() throws IOException {
		assertThatExceptionOfType(FieldTypeRequiredException.class)
				.isThrownBy(() -> new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
						.document(this.operationBuilder.response().content("<a>5</a>")
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE).build()));
	}

	@Test
	public void missingXmlResponseField() throws IOException {
		assertThatExceptionOfType(SnippetException.class)
				.isThrownBy(() -> new ResponseFieldsSnippet(
						Arrays.asList(fieldWithPath("a/b").description("one"), fieldWithPath("a").description("one")))
								.document(this.operationBuilder.response().content("<a></a>")
										.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE).build()))
				.withMessage("Fields with the following paths were not found in the payload: [a/b]");
	}

	@Test
	public void undocumentedXmlResponseFieldAndMissingXmlResponseField() throws IOException {
		assertThatExceptionOfType(SnippetException.class)
				.isThrownBy(() -> new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one")))
						.document(this.operationBuilder.response().content("<a><c>5</c></a>")
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE).build()))
				.withMessageStartingWith("The following parts of the payload were not documented:")
				.withMessageEndingWith("Fields with the following paths were not found in the payload: [a/b]");
	}

	@Test
	public void unsupportedContent() throws IOException {
		assertThatExceptionOfType(PayloadHandlingException.class)
				.isThrownBy(() -> new ResponseFieldsSnippet(Collections.<FieldDescriptor>emptyList())
						.document(this.operationBuilder.response().content("Some plain text")
								.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE).build()))
				.withMessage("Cannot handle text/plain content as it could not be parsed as JSON or XML");
	}

	@Test
	public void nonOptionalFieldBeneathArrayThatIsSometimesNull() throws IOException {
		assertThatExceptionOfType(SnippetException.class)
				.isThrownBy(() -> new ResponseFieldsSnippet(
						Arrays.asList(fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER),
								fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
										.document(this.operationBuilder.response()
												.content("{\"a\":[{\"b\": 1,\"c\": 2}, " + "{\"b\": null, \"c\": 2},"
														+ " {\"b\": 1,\"c\": 2}]}")
												.build()))
				.withMessageStartingWith("Fields with the following paths were not found in the payload: [a[].b]");
	}

	@Test
	public void nonOptionalFieldBeneathArrayThatIsSometimesAbsent() throws IOException {
		assertThatExceptionOfType(SnippetException.class)
				.isThrownBy(() -> new ResponseFieldsSnippet(
						Arrays.asList(fieldWithPath("a[].b").description("one").type(JsonFieldType.NUMBER),
								fieldWithPath("a[].c").description("two").type(JsonFieldType.NUMBER)))
										.document(this.operationBuilder.response()
												.content("{\"a\":[{\"b\": 1,\"c\": 2}, "
														+ "{\"c\": 2}, {\"b\": 1,\"c\": 2}]}")
												.build()))
				.withMessageStartingWith("Fields with the following paths were not found in the payload: [a[].b]");
	}

}
