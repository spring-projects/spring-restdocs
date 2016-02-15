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

package org.springframework.restdocs.headers;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.test.ExpectedSnippet;
import org.springframework.restdocs.test.OperationBuilder;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;

/**
 * Tests for failures when rendering {@link ResponseHeadersSnippet} due to missing or
 * undocumented headers.
 *
 * @author Andy Wilkinson
 */
public class ResponseHeadersSnippetFailureTests {

	@Rule
	public ExpectedSnippet snippet = new ExpectedSnippet(TemplateFormats.asciidoctor());

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void missingResponseHeader() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Headers with the following names were not found"
						+ " in the response: [Content-Type]"));
		new ResponseHeadersSnippet(
				Arrays.asList(headerWithName("Content-Type").description("one")))
						.document(new OperationBuilder("missing-response-headers",
								this.snippet.getOutputDirectory()).response().build());
	}

	@Test
	public void undocumentedResponseHeaderAndMissingResponseHeader() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(endsWith("Headers with the following names were not found"
						+ " in the response: [Content-Type]"));
		new ResponseHeadersSnippet(
				Arrays.asList(headerWithName("Content-Type").description("one")))
						.document(new OperationBuilder(
								"undocumented-response-header-and-missing-response-header",
								this.snippet.getOutputDirectory()).response()
										.header("X-Test", "test").build());
	}

}
