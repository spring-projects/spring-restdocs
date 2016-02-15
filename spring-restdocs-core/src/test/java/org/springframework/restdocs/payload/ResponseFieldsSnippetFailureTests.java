/*
 * Copyright 2012-2016 the original author or authors.
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
import org.springframework.restdocs.test.ExpectedSnippet;
import org.springframework.restdocs.test.OperationBuilder;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Tests for failures when rendering {@link ResponseFieldsSnippet} due to missing or
 * undocumented fields.
 *
 * @author Andy Wilkinson
 */
public class ResponseFieldsSnippetFailureTests {

	@Rule
	public ExpectedSnippet snippet = new ExpectedSnippet(TemplateFormats.asciidoctor());

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void undocumentedXmlResponseField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(startsWith(
				"The following parts of the payload were not" + " documented:"));
		new ResponseFieldsSnippet(
				Collections.<FieldDescriptor>emptyList())
						.document(
								new OperationBuilder("undocumented-xml-response-field",
										this.snippet.getOutputDirectory()).response()
												.content("<a><b>5</b></a>")
												.header(HttpHeaders.CONTENT_TYPE,
														MediaType.APPLICATION_XML_VALUE)
										.build());
	}

	@Test
	public void missingXmlAttribute() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Fields with the following paths were not found"
				+ " in the payload: [a/@id]"));
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a").description("one").type("b"),
						fieldWithPath("a/@id").description("two").type("c")))
								.document(new OperationBuilder("missing-xml-attribute",
										this.snippet.getOutputDirectory()).response()
												.content("<a>foo</a>")
												.header(HttpHeaders.CONTENT_TYPE,
														MediaType.APPLICATION_XML_VALUE)
										.build());
	}

	@Test
	public void documentedXmlAttributesAreRemoved() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo(
				String.format("The following parts of the payload were not documented:"
						+ "%n<a>bar</a>%n")));
		new ResponseFieldsSnippet(
				Arrays.asList(
						fieldWithPath("a/@id").description("one")
								.type("a"))).document(new OperationBuilder(
										"documented-attribute-is-removed",
										this.snippet.getOutputDirectory()).response()
												.content("<a id=\"foo\">bar</a>")
												.header(HttpHeaders.CONTENT_TYPE,
														MediaType.APPLICATION_XML_VALUE)
										.build());
	}

	@Test
	public void xmlResponseFieldWithNoType() throws IOException {
		this.thrown.expect(FieldTypeRequiredException.class);
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a").description("one")))
				.document(new OperationBuilder("xml-response-no-field-type",
						this.snippet.getOutputDirectory()).response().content("<a>5</a>")
								.header(HttpHeaders.CONTENT_TYPE,
										MediaType.APPLICATION_XML_VALUE)
								.build());
	}

	@Test
	public void missingXmlResponseField() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Fields with the following paths were not found"
				+ " in the payload: [a/b]"));
		new ResponseFieldsSnippet(Arrays.asList(fieldWithPath("a/b").description("one"),
				fieldWithPath("a").description("one")))
						.document(
								new OperationBuilder("missing-xml-response-field",
										this.snippet.getOutputDirectory()).response()
												.content("<a></a>")
												.header(HttpHeaders.CONTENT_TYPE,
														MediaType.APPLICATION_XML_VALUE)
										.build());
	}

	@Test
	public void undocumentedXmlResponseFieldAndMissingXmlResponseField()
			throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(startsWith(
				"The following parts of the payload were not" + " documented:"));
		this.thrown
				.expectMessage(endsWith("Fields with the following paths were not found"
						+ " in the payload: [a/b]"));
		new ResponseFieldsSnippet(
				Arrays.asList(fieldWithPath("a/b").description("one")))
						.document(
								new OperationBuilder(
										"undocumented-xml-request-field-and-missing-xml-request-field",
										this.snippet.getOutputDirectory()).response()
												.content("<a><c>5</c></a>")
												.header(HttpHeaders.CONTENT_TYPE,
														MediaType.APPLICATION_XML_VALUE)
										.build());
	}

}
