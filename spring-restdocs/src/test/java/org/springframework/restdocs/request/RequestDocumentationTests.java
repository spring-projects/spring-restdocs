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
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.documentPathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.documentQueryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;
import static org.springframework.restdocs.test.StubMvcResult.result;
import static org.springframework.restdocs.test.TestRequestBuilders.get;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.config.RestDocumentationContext;
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
	public void undocumentedQueryParameter() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown
				.expectMessage(equalTo("Query parameters with the following names were"
						+ " not documented: [a]"));
		documentQueryParameters("undocumented-query-parameter", null).handle(
				result(get("/").param("a", "alpha")));
	}

	@Test
	public void missingQueryParameter() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown
				.expectMessage(equalTo("Query parameters with the following names were"
						+ " not found in the request: [a]"));
		documentQueryParameters("missing-query-parameter", null,
				parameterWithName("a").description("one")).handle(result(get("/")));
	}

	@Test
	public void undocumentedAndMissingQueryParameters() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown
				.expectMessage(equalTo("Query parameters with the following names were"
						+ " not documented: [b]. Query parameters with the following"
						+ " names were not found in the request: [a]"));
		documentQueryParameters("undocumented-and-missing-query-parameters", null,
				parameterWithName("a").description("one")).handle(
				result(get("/").param("b", "bravo")));
	}

	@Test
	public void queryParameterSnippetFromRequestParameters() throws IOException {
		this.snippet.expectQueryParameters("query-parameter-snippet-request-parameters")
				.withContents(
						tableWithHeader("Parameter", "Description").row("a", "one").row(
								"b", "two"));
		documentQueryParameters("query-parameter-snippet-request-parameters", null,
				parameterWithName("a").description("one"),
				parameterWithName("b").description("two")).handle(
				result(get("/").param("a", "bravo").param("b", "bravo")));
	}

	@Test
	public void queryParameterSnippetFromRequestUriQueryString() throws IOException {
		this.snippet.expectQueryParameters(
				"query-parameter-snippet-request-uri-query-string").withContents(
				tableWithHeader("Parameter", "Description").row("a", "one").row("b",
						"two"));
		documentQueryParameters("query-parameter-snippet-request-uri-query-string", null,
				parameterWithName("a").description("one"),
				parameterWithName("b").description("two")).handle(
				result(get("/?a=alpha&b=bravo").requestAttr(
						RestDocumentationContext.class.getName(),
						new RestDocumentationContext())));
	}

	@Test
	public void queryParametersWithCustomDescriptorAttributes() throws IOException {
		this.snippet.expectQueryParameters(
				"query-parameters-with-custom-descriptor-attributes").withContents(
				tableWithHeader("Parameter", "Description", "Foo").row("a", "one",
						"alpha").row("b", "two", "bravo"));
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
		documentQueryParameters(
				"query-parameters-with-custom-descriptor-attributes",
				null,
				parameterWithName("a").description("one").attributes(
						key("foo").value("alpha")),
				parameterWithName("b").description("two").attributes(
						key("foo").value("bravo"))).handle(result(request));
	}

	@Test
	public void queryParametersWithCustomAttributes() throws IOException {
		this.snippet.expectQueryParameters("query-parameters-with-custom-attributes")
				.withContents(startsWith(".The title"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("query-parameters"))
				.thenReturn(
						new FileSystemResource(
								"src/test/resources/custom-snippet-templates/query-parameters-with-title.snippet"));
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(
				resolver));
		request.addParameter("a", "bravo");
		request.addParameter("b", "bravo");
		documentQueryParameters(
				"query-parameters-with-custom-attributes",
				attributes(key("title").value("The title")),
				parameterWithName("a").description("one").attributes(
						key("foo").value("alpha")),
				parameterWithName("b").description("two").attributes(
						key("foo").value("bravo"))).handle(result(request));

	}

	@Test
	public void undocumentedPathParameter() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown.expectMessage(equalTo("Path parameters with the following names were"
				+ " not documented: [a]"));
		documentPathParameters("undocumented-path-parameter", null).handle(
				result(get("/{a}/", "alpha")));
	}

	@Test
	public void missingPathParameter() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown.expectMessage(equalTo("Path parameters with the following names were"
				+ " not found in the request: [a]"));
		documentPathParameters("missing-path-parameter", null,
				parameterWithName("a").description("one")).handle(result(get("/")));
	}

	@Test
	public void undocumentedAndMissingPathParameters() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown.expectMessage(equalTo("Path parameters with the following names were"
				+ " not documented: [b]. Path parameters with the following"
				+ " names were not found in the request: [a]"));
		documentPathParameters("undocumented-and-missing-path-parameters", null,
				parameterWithName("a").description("one")).handle(
				result(get("/{b}", "bravo")));
	}

	@Test
	public void pathParameters() throws IOException {
		this.snippet.expectPathParameters("path-parameters").withContents(
				tableWithHeader("Parameter", "Description").row("a", "one").row("b",
						"two"));
		documentPathParameters("path-parameters", null,
				parameterWithName("a").description("one"),
				parameterWithName("b").description("two")).handle(
				result(get("/{a}/{b}", "alpha", "banana").requestAttr(
						RestDocumentationContext.class.getName(),
						new RestDocumentationContext())));
	}

	@Test
	public void pathParametersWithCustomDescriptorAttributes() throws IOException {
		this.snippet.expectPathParameters(
				"path-parameters-with-custom-descriptor-attributes").withContents(
				tableWithHeader("Parameter", "Description", "Foo").row("a", "one",
						"alpha").row("b", "two", "bravo"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("path-parameters"))
				.thenReturn(
						new FileSystemResource(
								"src/test/resources/custom-snippet-templates/path-parameters-with-extra-column.snippet"));
		documentPathParameters(
				"path-parameters-with-custom-descriptor-attributes",
				null,
				parameterWithName("a").description("one").attributes(
						key("foo").value("alpha")),
				parameterWithName("b").description("two").attributes(
						key("foo").value("bravo"))).handle(
				result(get("{a}/{b}", "alpha", "bravo").requestAttr(
						TemplateEngine.class.getName(),
						new MustacheTemplateEngine(resolver))));
	}

	@Test
	public void pathParametersWithCustomAttributes() throws IOException {
		this.snippet.expectPathParameters("path-parameters-with-custom-attributes")
				.withContents(startsWith(".The title"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("path-parameters"))
				.thenReturn(
						new FileSystemResource(
								"src/test/resources/custom-snippet-templates/path-parameters-with-title.snippet"));
		documentPathParameters(
				"path-parameters-with-custom-attributes",
				attributes(key("title").value("The title")),
				parameterWithName("a").description("one").attributes(
						key("foo").value("alpha")),
				parameterWithName("b").description("two").attributes(
						key("foo").value("bravo"))).handle(
				result(get("{a}/{b}", "alpha", "bravo").requestAttr(
						TemplateEngine.class.getName(),
						new MustacheTemplateEngine(resolver))));

	}
}
