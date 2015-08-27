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
import static org.springframework.restdocs.test.SnippetMatchers.tableWithTitleAndHeader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;
import org.springframework.restdocs.test.OperationBuilder;

/**
 * Tests for {@link PathParametersSnippet}
 * 
 * @author awilkinson
 *
 */
public class PathParametersSnippetTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public ExpectedSnippet snippet = new ExpectedSnippet();

	@Test
	public void undocumentedPathParameter() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Path parameters with the following names were"
				+ " not documented: [a]"));
		new PathParametersSnippet(Collections.<ParameterDescriptor> emptyList())
				.document(new OperationBuilder("undocumented-path-parameter").attribute(
						"org.springframework.restdocs.urlTemplate", "/{a}/").build());
	}

	@Test
	public void missingPathParameter() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Path parameters with the following names were"
				+ " not found in the request: [a]"));
		new PathParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one")))
				.document(new OperationBuilder("missing-path-parameter").attribute(
						"org.springframework.restdocs.urlTemplate", "/").build());
	}

	@Test
	public void undocumentedAndMissingPathParameters() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Path parameters with the following names were"
				+ " not documented: [b]. Path parameters with the following"
				+ " names were not found in the request: [a]"));
		new PathParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one")))
				.document(new OperationBuilder("undocumented-and-missing-path-parameters")
						.attribute("org.springframework.restdocs.urlTemplate", "/{b}")
						.build());
	}

	@Test
	public void pathParameters() throws IOException {
		this.snippet.expectPathParameters("path-parameters").withContents(
				tableWithTitleAndHeader("/{a}/{b}", "Parameter", "Description").row("a",
						"one").row("b", "two"));
		new PathParametersSnippet(Arrays.asList(
				parameterWithName("a").description("one"), parameterWithName("b")
						.description("two"))).document(new OperationBuilder(
				"path-parameters").attribute("org.springframework.restdocs.urlTemplate",
				"/{a}/{b}").build());
	}

	@Test
	public void pathParametersWithQueryString() throws IOException {
		this.snippet.expectPathParameters("path-parameters-with-query-string")
				.withContents(
						tableWithTitleAndHeader("/{a}/{b}", "Parameter", "Description")
								.row("a", "one").row("b", "two"));
		new PathParametersSnippet(Arrays.asList(
				parameterWithName("a").description("one"), parameterWithName("b")
						.description("two"))).document(new OperationBuilder(
				"path-parameters-with-query-string").attribute(
				"org.springframework.restdocs.urlTemplate", "/{a}/{b}?foo=bar").build());
	}

	@Test
	public void pathParametersWithCustomDescriptorAttributes() throws IOException {
		this.snippet.expectPathParameters(
				"path-parameters-with-custom-descriptor-attributes").withContents(
				tableWithHeader("Parameter", "Description", "Foo").row("a", "one",
						"alpha").row("b", "two", "bravo"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("path-parameters")).thenReturn(
				snippetResource("path-parameters-with-extra-column"));
		new PathParametersSnippet(Arrays.asList(parameterWithName("a").description("one")
				.attributes(key("foo").value("alpha")), parameterWithName("b")
				.description("two").attributes(key("foo").value("bravo"))))
				.document(new OperationBuilder(
						"path-parameters-with-custom-descriptor-attributes")
						.attribute("org.springframework.restdocs.urlTemplate", "/{a}/{b}")
						.attribute(TemplateEngine.class.getName(),
								new MustacheTemplateEngine(resolver)).build());
	}

	@Test
	public void pathParametersWithCustomAttributes() throws IOException {
		this.snippet.expectPathParameters("path-parameters-with-custom-attributes")
				.withContents(startsWith(".The title"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("path-parameters")).thenReturn(
				snippetResource("path-parameters-with-title"));
		new PathParametersSnippet(
				attributes(key("title").value("The title")),
				Arrays.asList(
						parameterWithName("a").description("one").attributes(
								key("foo").value("alpha")), parameterWithName("b")
								.description("two").attributes(key("foo").value("bravo"))))
				.document(new OperationBuilder("path-parameters-with-custom-attributes")
						.attribute("org.springframework.restdocs.urlTemplate", "/{a}/{b}")
						.attribute(TemplateEngine.class.getName(),
								new MustacheTemplateEngine(resolver)).build());

	}

	private FileSystemResource snippetResource(String name) {
		return new FileSystemResource("src/test/resources/custom-snippet-templates/"
				+ name + ".snippet");
	}

}
