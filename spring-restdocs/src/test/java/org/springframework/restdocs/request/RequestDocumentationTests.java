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

package org.springframework.restdocs.request;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.documentQueryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;
import static org.springframework.restdocs.test.StubMvcResult.result;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.snippet.SnippetGenerationException;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;

/**
 * Requests for {@link RequestDocumentation}
 * 
 * @author Andy Wilkinson
 */
public class RequestDocumentationTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public ExpectedSnippet snippet = new ExpectedSnippet();

	@Test
	public void undocumentedParameter() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown
				.expectMessage(equalTo("Query parameters with the following names were"
						+ " not documented: [a]"));
		documentQueryParameters("undocumented-parameter").handle(
				result(get("/").param("a", "alpha")));
	}

	@Test
	public void missingParameter() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown
				.expectMessage(equalTo("Query parameters with the following names were"
						+ " not found in the request: [a]"));
		documentQueryParameters("missing-parameter",
				parameterWithName("a").description("one")).handle(result(get("/")));
	}

	@Test
	public void undocumentedParameterAndMissingParameter() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown
				.expectMessage(equalTo("Query parameters with the following names were"
						+ " not documented: [b]. Query parameters with the following"
						+ " names were not found in the request: [a]"));
		documentQueryParameters("undocumented-parameter-missing-parameter",
				parameterWithName("a").description("one")).handle(
				result(get("/").param("b", "bravo")));
	}

	@Test
	public void parameterSnippetFromRequestParameters() throws IOException {
		this.snippet.expectQueryParameters("parameter-snippet-request-parameters")
				.withContents(
						tableWithHeader("Parameter", "Description").row("a", "one").row(
								"b", "two"));
		documentQueryParameters("parameter-snippet-request-parameters",
				parameterWithName("a").description("one"),
				parameterWithName("b").description("two")).handle(
				result(get("/").param("a", "bravo").param("b", "bravo")));
	}

	@Test
	public void parameterSnippetFromRequestUriQueryString() throws IOException {
		this.snippet.expectQueryParameters("parameter-snippet-request-uri-query-string")
				.withContents(
						tableWithHeader("Parameter", "Description").row("a", "one").row(
								"b", "two"));
		documentQueryParameters("parameter-snippet-request-uri-query-string",
				parameterWithName("a").description("one"),
				parameterWithName("b").description("two")).handle(
				result(get("/?a=alpha&b=bravo")));
	}

	@Test
	public void parametersWithCustomAttributes() throws IOException {
		this.snippet.expectQueryParameters("parameters-with-custom-attributes")
				.withContents(
						tableWithHeader("Parameter", "Description", "Foo").row("a",
								"one", "alpha").row("b", "two", "bravo"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("query-parameters"))
				.thenReturn(
						new FileSystemResource(
								"src/test/resources/custom-snippet-templates/query-parameters-with-extra-column.snippet"));
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(
				resolver));
		request.addParameter("a", "bravo");
		request.addParameter("b", "bravo");
		documentQueryParameters("parameters-with-custom-attributes",
				parameterWithName("a").description("one").attribute("foo", "alpha"),
				parameterWithName("b").description("two").attribute("foo", "bravo"))
				.handle(result(request));

	}
}
