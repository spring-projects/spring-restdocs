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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;
import static org.springframework.restdocs.test.StubMvcResult.result;
import static org.springframework.restdocs.test.TestRequestBuilders.get;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.config.RestDocumentationContext;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;

/**
 * Tests for {@link QueryParametersSnippet}
 *
 * @author Andy Wilkinson
 */
public class QueryParametersSnippetTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public ExpectedSnippet snippet = new ExpectedSnippet();

	@Test
	public void undocumentedQueryParameter() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Query parameters with the following names were"
						+ " not documented: [a]"));
		new QueryParametersSnippet(Collections.<ParameterDescriptor> emptyList())
				.document("undocumented-query-parameter",
						result(get("/").param("a", "alpha")));
	}

	@Test
	public void missingQueryParameter() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Query parameters with the following names were"
						+ " not found in the request: [a]"));
		new QueryParametersSnippet(Arrays.asList(parameterWithName("a")
				.description("one"))).document("missing-query-parameter",
				result(get("/")));
	}

	@Test
	public void undocumentedAndMissingQueryParameters() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Query parameters with the following names were"
						+ " not documented: [b]. Query parameters with the following"
						+ " names were not found in the request: [a]"));
		new QueryParametersSnippet(Arrays.asList(parameterWithName("a")
				.description("one"))).document(
				"undocumented-and-missing-query-parameters",
				result(get("/").param("b", "bravo")));
	}

	@Test
	public void queryParameterSnippetFromRequestParameters() throws IOException {
		this.snippet.expectQueryParameters("query-parameter-snippet-request-parameters")
				.withContents(
						tableWithHeader("Parameter", "Description").row("a", "one").row(
								"b", "two"));
		new QueryParametersSnippet(Arrays.asList(parameterWithName("a")
				.description("one"), parameterWithName("b").description("two")))
				.document("query-parameter-snippet-request-parameters", result(get("/")
						.param("a", "bravo").param("b", "bravo")));
	}

	@Test
	public void queryParameterSnippetFromRequestUriQueryString() throws IOException {
		this.snippet.expectQueryParameters(
				"query-parameter-snippet-request-uri-query-string").withContents(
				tableWithHeader("Parameter", "Description").row("a", "one").row("b",
						"two"));
		new QueryParametersSnippet(Arrays.asList(parameterWithName("a")
				.description("one"), parameterWithName("b").description("two")))
				.document(
						"query-parameter-snippet-request-uri-query-string",
						result(get("/?a=alpha&b=bravo").requestAttr(
								RestDocumentationContext.class.getName(),
								new RestDocumentationContext(null))));
	}

	@Test
	public void queryParametersWithCustomDescriptorAttributes() throws IOException {
		this.snippet.expectQueryParameters(
				"query-parameters-with-custom-descriptor-attributes").withContents(
				tableWithHeader("Parameter", "Description", "Foo").row("a", "one",
						"alpha").row("b", "two", "bravo"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("query-parameters")).thenReturn(
				snippetResource("query-parameters-with-extra-column"));
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(
				resolver));
		request.addParameter("a", "bravo");
		request.addParameter("b", "bravo");
		new QueryParametersSnippet(Arrays.asList(
				parameterWithName("a").description("one").attributes(
						key("foo").value("alpha")),
				parameterWithName("b").description("two").attributes(
						key("foo").value("bravo")))).document(
				"query-parameters-with-custom-descriptor-attributes", result(request));
	}

	@Test
	public void queryParametersWithCustomAttributes() throws IOException {
		this.snippet.expectQueryParameters("query-parameters-with-custom-attributes")
				.withContents(startsWith(".The title"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("query-parameters")).thenReturn(
				snippetResource("query-parameters-with-title"));
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(
				resolver));
		request.addParameter("a", "bravo");
		request.addParameter("b", "bravo");
		new QueryParametersSnippet(
				attributes(key("title").value("The title")),
				Arrays.asList(
						parameterWithName("a").description("one").attributes(
								key("foo").value("alpha")), parameterWithName("b")
								.description("two").attributes(key("foo").value("bravo"))))
				.document("query-parameters-with-custom-attributes", result(request));
	}

	private FileSystemResource snippetResource(String name) {
		return new FileSystemResource("src/test/resources/custom-snippet-templates/"
				+ name + ".snippet");
	}

}
