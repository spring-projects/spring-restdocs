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

import org.junit.Test;

import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link RequestParametersSnippet}.
 *
 * @author Andy Wilkinson
 */
public class RequestParametersSnippetTests extends AbstractSnippetTests {

	public RequestParametersSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void requestParameters() throws IOException {
		this.snippet.expectRequestParameters("request-parameters")
				.withContents(tableWithHeader("Parameter", "Description").row("a", "one")
						.row("b", "two"));
		new RequestParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one"),
						parameterWithName("b").description("two")))
								.document(operationBuilder("request-parameters")
										.request("http://localhost").param("a", "bravo")
										.param("b", "bravo").build());
	}

	@Test
	public void requestParameterWithNoValue() throws IOException {
		this.snippet.expectRequestParameters("request-parameter-with-no-value")
				.withContents(
						tableWithHeader("Parameter", "Description").row("a", "one"));
		new RequestParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one")))
						.document(operationBuilder("request-parameter-with-no-value")
								.request("http://localhost").param("a").build());
	}

	@Test
	public void ignoredRequestParameter() throws IOException {
		this.snippet.expectRequestParameters("ignored-request-parameter").withContents(
				tableWithHeader("Parameter", "Description").row("b", "two"));
		new RequestParametersSnippet(Arrays.asList(parameterWithName("a").ignored(),
				parameterWithName("b").description("two")))
						.document(operationBuilder("ignored-request-parameter")
								.request("http://localhost").param("a", "bravo")
								.param("b", "bravo").build());
	}

	@Test
	public void allUndocumentedRequestParametersCanBeIgnored() throws IOException {
		this.snippet.expectRequestParameters("ignore-all-undocumented").withContents(
				tableWithHeader("Parameter", "Description").row("b", "two"));
		new RequestParametersSnippet(
				Arrays.asList(parameterWithName("b").description("two")), true)
						.document(operationBuilder("ignore-all-undocumented")
								.request("http://localhost").param("a", "bravo")
								.param("b", "bravo").build());
	}

	@Test
	public void missingOptionalRequestParameter() throws IOException {
		this.snippet.expectRequestParameters("missing-optional-request-parameter")
				.withContents(tableWithHeader("Parameter", "Description").row("a", "one")
						.row("b", "two"));
		new RequestParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one").optional(),
						parameterWithName("b").description("two"))).document(
								operationBuilder("missing-optional-request-parameter")
										.request("http://localhost").param("b", "bravo")
										.build());
	}

	@Test
	public void requestParametersWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-parameters"))
				.willReturn(snippetResource("request-parameters-with-title"));
		this.snippet.expectRequestParameters("request-parameters-with-custom-attributes")
				.withContents(containsString("The title"));

		new RequestParametersSnippet(
				Arrays.asList(
						parameterWithName("a").description("one")
								.attributes(key("foo").value("alpha")),
				parameterWithName("b").description("two")
						.attributes(key("foo").value("bravo"))),
				attributes(key("title").value("The title"))).document(
						operationBuilder("request-parameters-with-custom-attributes")
								.attribute(TemplateEngine.class.getName(),
										new MustacheTemplateEngine(resolver))
								.request("http://localhost").param("a", "alpha")
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
								.document(operationBuilder(
										"request-parameters-with-custom-descriptor-attributes")
												.attribute(TemplateEngine.class.getName(),
														new MustacheTemplateEngine(
																resolver))
												.request("http://localhost")
												.param("a", "alpha").param("b", "bravo")
												.build());
	}

	@Test
	public void additionalDescriptors() throws IOException {
		this.snippet.expectRequestParameters("additional-descriptors")
				.withContents(tableWithHeader("Parameter", "Description").row("a", "one")
						.row("b", "two"));
		RequestDocumentation.requestParameters(parameterWithName("a").description("one"))
				.and(parameterWithName("b").description("two"))
				.document(operationBuilder("additional-descriptors")
						.request("http://localhost").param("a", "bravo")
						.param("b", "bravo").build());
	}

}
