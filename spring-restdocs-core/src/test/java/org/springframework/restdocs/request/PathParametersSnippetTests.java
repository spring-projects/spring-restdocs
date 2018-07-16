/*
 * Copyright 2014-2018 the original author or authors.
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
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link PathParametersSnippet}.
 *
 * @author Andy Wilkinson
 */
public class PathParametersSnippetTests extends AbstractSnippetTests {

	public PathParametersSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void pathParameters() throws IOException {
		this.snippets.expectPathParameters().withContents(
				tableWithTitleAndHeader(getTitle(), "Parameter", "Description")
						.row("`a`", "one").row("`b`", "two"));
		new PathParametersSnippet(Arrays.asList(parameterWithName("a").description("one"),
				parameterWithName("b").description("two"))).document(this.operationBuilder
						.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"/{a}/{b}")
						.build());
	}

	@Test
	public void ignoredPathParameter() throws IOException {
		this.snippets.expectPathParameters().withContents(
				tableWithTitleAndHeader(getTitle(), "Parameter", "Description").row("`b`",
						"two"));
		new PathParametersSnippet(Arrays.asList(parameterWithName("a").ignored(),
				parameterWithName("b").description("two"))).document(this.operationBuilder
						.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"/{a}/{b}")
						.build());
	}

	@Test
	public void allUndocumentedPathParametersCanBeIgnored() throws IOException {
		this.snippets.expectPathParameters().withContents(
				tableWithTitleAndHeader(getTitle(), "Parameter", "Description").row("`b`",
						"two"));
		new PathParametersSnippet(
				Arrays.asList(parameterWithName("b").description("two")), true)
						.document(this.operationBuilder.attribute(
								RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"/{a}/{b}").build());
	}

	@Test
	public void missingOptionalPathParameter() throws IOException {
		this.snippets.expectPathParameters()
				.withContents(tableWithTitleAndHeader(
						this.templateFormat == TemplateFormats.asciidoctor() ? "+/{a}+"
								: "`/{a}`",
						"Parameter", "Description").row("`a`", "one").row("`b`", "two"));
		new PathParametersSnippet(Arrays.asList(parameterWithName("a").description("one"),
				parameterWithName("b").description("two").optional()))
						.document(this.operationBuilder.attribute(
								RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"/{a}").build());
	}

	@Test
	public void presentOptionalPathParameter() throws IOException {
		this.snippets.expectPathParameters()
				.withContents(tableWithTitleAndHeader(
						this.templateFormat == TemplateFormats.asciidoctor() ? "+/{a}+"
								: "`/{a}`",
						"Parameter", "Description").row("`a`", "one"));
		new PathParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one").optional()))
						.document(this.operationBuilder.attribute(
								RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"/{a}").build());
	}

	@Test
	public void pathParametersWithQueryString() throws IOException {
		this.snippets.expectPathParameters().withContents(
				tableWithTitleAndHeader(getTitle(), "Parameter", "Description")
						.row("`a`", "one").row("`b`", "two"));
		new PathParametersSnippet(Arrays.asList(parameterWithName("a").description("one"),
				parameterWithName("b").description("two"))).document(this.operationBuilder
						.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"/{a}/{b}?foo=bar")
						.build());
	}

	@Test
	public void pathParametersWithQueryStringWithParameters() throws IOException {
		this.snippets.expectPathParameters().withContents(
				tableWithTitleAndHeader(getTitle(), "Parameter", "Description")
						.row("`a`", "one").row("`b`", "two"));
		new PathParametersSnippet(Arrays.asList(parameterWithName("a").description("one"),
				parameterWithName("b").description("two"))).document(this.operationBuilder
						.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"/{a}/{b}?foo={c}")
						.build());
	}

	@Test
	public void pathParametersWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("path-parameters"))
				.willReturn(snippetResource("path-parameters-with-title"));
		this.snippets.expectPathParameters().withContents(containsString("The title"));

		new PathParametersSnippet(
				Arrays.asList(
						parameterWithName("a").description("one")
								.attributes(key("foo").value("alpha")),
						parameterWithName("b").description("two")
								.attributes(key("foo").value("bravo"))),
				attributes(key("title").value("The title")))
						.document(this.operationBuilder.attribute(
								RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"/{a}/{b}")
								.attribute(TemplateEngine.class.getName(),
										new MustacheTemplateEngine(resolver))
								.build());

	}

	@Test
	public void pathParametersWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("path-parameters"))
				.willReturn(snippetResource("path-parameters-with-extra-column"));
		this.snippets.expectPathParameters()
				.withContents(tableWithHeader("Parameter", "Description", "Foo")
						.row("a", "one", "alpha").row("b", "two", "bravo"));

		new PathParametersSnippet(Arrays.asList(
				parameterWithName("a").description("one")
						.attributes(key("foo").value("alpha")),
				parameterWithName("b").description("two").attributes(key("foo")
						.value("bravo")))).document(this.operationBuilder.attribute(
								RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"/{a}/{b}")
								.attribute(TemplateEngine.class.getName(),
										new MustacheTemplateEngine(resolver))
								.build());
	}

	@Test
	public void additionalDescriptors() throws IOException {
		this.snippets.expectPathParameters().withContents(
				tableWithTitleAndHeader(getTitle(), "Parameter", "Description")
						.row("`a`", "one").row("`b`", "two"));
		RequestDocumentation.pathParameters(parameterWithName("a").description("one"))
				.and(parameterWithName("b").description("two"))
				.document(this.operationBuilder
						.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"/{a}/{b}")
						.build());
	}

	@Test
	public void pathParametersWithEscapedContent() throws IOException {
		this.snippets.expectPathParameters()
				.withContents(tableWithTitleAndHeader(getTitle("{Foo|Bar}"), "Parameter",
						"Description").row(escapeIfNecessary("`Foo|Bar`"),
								escapeIfNecessary("one|two")));

		RequestDocumentation
				.pathParameters(parameterWithName("Foo|Bar").description("one|two"))
				.document(this.operationBuilder
						.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"{Foo|Bar}")
						.build());
	}

	private String escapeIfNecessary(String input) {
		if (this.templateFormat.equals(TemplateFormats.markdown())) {
			return input;
		}
		return input.replace("|", "\\|");
	}

	private String getTitle() {
		return getTitle("/{a}/{b}");
	}

	private String getTitle(String title) {
		if (this.templateFormat.equals(TemplateFormats.asciidoctor())) {
			return "+" + title + "+";
		}
		return "`" + title + "`";
	}

}
