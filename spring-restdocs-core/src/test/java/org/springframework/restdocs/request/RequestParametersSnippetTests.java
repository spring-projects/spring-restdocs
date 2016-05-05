/*
 * Copyright 2014-2016 the original author or authors.
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

import org.springframework.core.io.FileSystemResource;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;
import org.springframework.restdocs.test.OperationBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;

/**
 * Tests for {@link RequestParametersSnippet}.
 *
 * @author Andy Wilkinson
 */
public class RequestParametersSnippetTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public ExpectedSnippet snippet = new ExpectedSnippet();

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

	@Test
	public void requestParameters() throws IOException {
		this.snippet.expectRequestParameters("request-parameters")
				.withContents(tableWithHeader("Parameter", "Description").row("a", "one")
						.row("b", "two"));
		new RequestParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one"),
						parameterWithName("b").description("two")))
								.document(new OperationBuilder("request-parameters",
										this.snippet.getOutputDirectory())
												.request("http://localhost")
												.param("a", "bravo").param("b", "bravo")
												.build());
	}

	@Test
	public void requestParameterWithNoValue() throws IOException {
		this.snippet.expectRequestParameters("request-parameter-with-no-value")
				.withContents(
						tableWithHeader("Parameter", "Description").row("a", "one"));
		new RequestParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one")))
						.document(new OperationBuilder("request-parameter-with-no-value",
								this.snippet.getOutputDirectory())
										.request("http://localhost").param("a").build());
	}

	@Test
	public void ignoredRequestParameter() throws IOException {
		this.snippet.expectRequestParameters("ignored-request-parameter").withContents(
				tableWithHeader("Parameter", "Description").row("b", "two"));
		new RequestParametersSnippet(Arrays.asList(parameterWithName("a").ignored(),
				parameterWithName("b").description("two")))
						.document(new OperationBuilder("ignored-request-parameter",
								this.snippet.getOutputDirectory())
										.request("http://localhost").param("a", "bravo")
										.param("b", "bravo").build());
	}

	@Test
	public void requestParametersWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-parameters"))
				.willReturn(snippetResource("request-parameters-with-extra-column"));
		this.snippet
				.expectRequestParameters(
						"request-parameters-with-custom-descriptor-attributes")
				.withContents(tableWithHeader("Parameter", "Description", "Foo")
						.row("a", "one", "alpha").row("b", "two", "bravo"));

		new RequestParametersSnippet(Arrays.asList(
				parameterWithName("a").description("one")
						.attributes(key("foo").value("alpha")),
				parameterWithName("b").description("two")
						.attributes(key("foo").value("bravo"))))
								.document(new OperationBuilder(
										"request-parameters-with-custom-descriptor-attributes",
										this.snippet
												.getOutputDirectory())
														.attribute(
																TemplateEngine.class
																		.getName(),
																new MustacheTemplateEngine(
																		resolver))
														.request("http://localhost")
														.param("a", "alpha")
														.param("b", "bravo").build());
	}

	@Test
	public void requestParametersWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-parameters"))
				.willReturn(snippetResource("request-parameters-with-title"));
		this.snippet.expectRequestParameters("request-parameters-with-custom-attributes")
				.withContents(startsWith(".The title"));

		new RequestParametersSnippet(
				Arrays.asList(
						parameterWithName("a").description("one")
								.attributes(key("foo").value("alpha")),
						parameterWithName("b").description("two")
								.attributes(key("foo").value("bravo"))),
				attributes(key("title").value("The title"))).document(
						new OperationBuilder("request-parameters-with-custom-attributes",
								this.snippet.getOutputDirectory())
										.attribute(TemplateEngine.class.getName(),
												new MustacheTemplateEngine(resolver))
										.request("http://localhost").param("a", "alpha")
										.param("b", "bravo").build());
	}

	private FileSystemResource snippetResource(String name) {
		return new FileSystemResource(
				"src/test/resources/custom-snippet-templates/" + name + ".snippet");
	}

}
