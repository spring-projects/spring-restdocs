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

package org.springframework.restdocs.request;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.test.ExpectedSnippet;
import org.springframework.restdocs.test.OperationBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

/**
 * Tests for failures when rendering {@link RequestParametersSnippet} due to missing or
 * undocumented request parameters.
 *
 * @author Andy Wilkinson
 */
public class RequestParametersSnippetFailureTests {

	@Rule
	public ExpectedSnippet snippet = new ExpectedSnippet(TemplateFormats.asciidoctor());

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void undocumentedParameter() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Request parameters with the following names were"
						+ " not documented: [a]"));
		new RequestParametersSnippet(Collections.<ParameterDescriptor>emptyList())
				.document(new OperationBuilder("undocumented-parameter",
						this.snippet.getOutputDirectory()).request("http://localhost")
								.param("a", "alpha").build());
	}

	@Test
	public void missingParameter() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Request parameters with the following names were"
						+ " not found in the request: [a]"));
		new RequestParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one")))
						.document(new OperationBuilder("missing-parameter",
								this.snippet.getOutputDirectory())
										.request("http://localhost").build());
	}

	@Test
	public void undocumentedAndMissingParameters() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Request parameters with the following names were"
						+ " not documented: [b]. Request parameters with the following"
						+ " names were not found in the request: [a]"));
		new RequestParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one"))).document(
						new OperationBuilder("undocumented-and-missing-parameters",
								this.snippet.getOutputDirectory())
										.request("http://localhost").param("b", "bravo")
										.build());
	}

}
