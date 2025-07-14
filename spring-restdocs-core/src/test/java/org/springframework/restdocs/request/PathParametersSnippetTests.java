/*
 * Copyright 2014-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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

import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTemplate;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link PathParametersSnippet}.
 *
 * @author Andy Wilkinson
 */
class PathParametersSnippetTests {

	@RenderedSnippetTest
	void pathParameters(OperationBuilder operationBuilder, AssertableSnippets snippets, TemplateFormat templateFormat)
			throws IOException {
		new PathParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one"), parameterWithName("b").description("two")))
			.document(operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{a}/{b}")
				.build());
		assertThat(snippets.pathParameters())
			.isTable((table) -> table.withTitleAndHeader(getTitle(templateFormat), "Parameter", "Description")
				.row("`a`", "one")
				.row("`b`", "two"));
	}

	@RenderedSnippetTest
	void ignoredPathParameter(OperationBuilder operationBuilder, AssertableSnippets snippets,
			TemplateFormat templateFormat) throws IOException {
		new PathParametersSnippet(
				Arrays.asList(parameterWithName("a").ignored(), parameterWithName("b").description("two")))
			.document(operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{a}/{b}")
				.build());
		assertThat(snippets.pathParameters())
			.isTable((table) -> table.withTitleAndHeader(getTitle(templateFormat), "Parameter", "Description")
				.row("`b`", "two"));
	}

	@RenderedSnippetTest
	void allUndocumentedPathParametersCanBeIgnored(OperationBuilder operationBuilder, AssertableSnippets snippets,
			TemplateFormat templateFormat) throws IOException {
		new PathParametersSnippet(Arrays.asList(parameterWithName("b").description("two")), true).document(
				operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{a}/{b}").build());
		assertThat(snippets.pathParameters())
			.isTable((table) -> table.withTitleAndHeader(getTitle(templateFormat), "Parameter", "Description")
				.row("`b`", "two"));
	}

	@RenderedSnippetTest
	void missingOptionalPathParameter(OperationBuilder operationBuilder, AssertableSnippets snippets,
			TemplateFormat templateFormat) throws IOException {
		new PathParametersSnippet(Arrays.asList(parameterWithName("a").description("one"),
				parameterWithName("b").description("two").optional()))
			.document(
					operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{a}").build());
		assertThat(snippets.pathParameters())
			.isTable((table) -> table.withTitleAndHeader(getTitle(templateFormat, "/{a}"), "Parameter", "Description")
				.row("`a`", "one")
				.row("`b`", "two"));
	}

	@RenderedSnippetTest
	void presentOptionalPathParameter(OperationBuilder operationBuilder, AssertableSnippets snippets,
			TemplateFormat templateFormat) throws IOException {
		new PathParametersSnippet(Arrays.asList(parameterWithName("a").description("one").optional())).document(
				operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{a}").build());
		assertThat(snippets.pathParameters())
			.isTable((table) -> table.withTitleAndHeader(getTitle(templateFormat, "/{a}"), "Parameter", "Description")
				.row("`a`", "one"));
	}

	@RenderedSnippetTest
	void pathParametersWithQueryString(OperationBuilder operationBuilder, AssertableSnippets snippets,
			TemplateFormat templateFormat) throws IOException {
		new PathParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one"), parameterWithName("b").description("two")))
			.document(operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{a}/{b}?foo=bar")
				.build());
		assertThat(snippets.pathParameters())
			.isTable((table) -> table.withTitleAndHeader(getTitle(templateFormat), "Parameter", "Description")
				.row("`a`", "one")
				.row("`b`", "two"));
	}

	@RenderedSnippetTest
	void pathParametersWithQueryStringWithParameters(OperationBuilder operationBuilder, AssertableSnippets snippets,
			TemplateFormat templateFormat) throws IOException {
		new PathParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one"), parameterWithName("b").description("two")))
			.document(operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{a}/{b}?foo={c}")
				.build());
		assertThat(snippets.pathParameters())
			.isTable((table) -> table.withTitleAndHeader(getTitle(templateFormat), "Parameter", "Description")
				.row("`a`", "one")
				.row("`b`", "two"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "path-parameters", template = "path-parameters-with-title")
	void pathParametersWithCustomAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets,
			TemplateFormat templateFormat) throws IOException {
		new PathParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one").attributes(key("foo").value("alpha")),
						parameterWithName("b").description("two").attributes(key("foo").value("bravo"))),
				attributes(key("title").value("The title")))
			.document(operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{a}/{b}")
				.build());
		assertThat(snippets.pathParameters()).contains("The title");
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "path-parameters", template = "path-parameters-with-extra-column")
	void pathParametersWithCustomDescriptorAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets,
			TemplateFormat templateFormat) throws IOException {
		new PathParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one").attributes(key("foo").value("alpha")),
						parameterWithName("b").description("two").attributes(key("foo").value("bravo"))))
			.document(operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{a}/{b}")
				.build());
		assertThat(snippets.pathParameters()).isTable((table) -> table.withHeader("Parameter", "Description", "Foo")
			.row("a", "one", "alpha")
			.row("b", "two", "bravo"));
	}

	@RenderedSnippetTest
	void additionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets,
			TemplateFormat templateFormat) throws IOException {
		RequestDocumentation.pathParameters(parameterWithName("a").description("one"))
			.and(parameterWithName("b").description("two"))
			.document(operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{a}/{b}")
				.build());
		assertThat(snippets.pathParameters()).isTable(
				(table) -> table.withTitleAndHeader(getTitle(templateFormat, "/{a}/{b}"), "Parameter", "Description")
					.row("`a`", "one")
					.row("`b`", "two"));
	}

	@RenderedSnippetTest
	void additionalDescriptorsWithRelaxedRequestParameters(OperationBuilder operationBuilder,
			AssertableSnippets snippets, TemplateFormat templateFormat) throws IOException {
		RequestDocumentation.relaxedPathParameters(parameterWithName("a").description("one"))
			.and(parameterWithName("b").description("two"))
			.document(operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{a}/{b}/{c}")
				.build());
		assertThat(snippets.pathParameters()).isTable((table) -> table
			.withTitleAndHeader(getTitle(templateFormat, "/{a}/{b}/{c}"), "Parameter", "Description")
			.row("`a`", "one")
			.row("`b`", "two"));
	}

	@RenderedSnippetTest
	void pathParametersWithEscapedContent(OperationBuilder operationBuilder, AssertableSnippets snippets,
			TemplateFormat templateFormat) throws IOException {
		RequestDocumentation.pathParameters(parameterWithName("Foo|Bar").description("one|two"))
			.document(operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "{Foo|Bar}")
				.build());
		assertThat(snippets.pathParameters()).isTable(
				(table) -> table.withTitleAndHeader(getTitle(templateFormat, "{Foo|Bar}"), "Parameter", "Description")
					.row("`Foo|Bar`", "one|two"));
	}

	@SnippetTest
	void undocumentedPathParameter(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new PathParametersSnippet(Collections.<ParameterDescriptor>emptyList())
				.document(operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{a}/")
					.build()))
			.withMessage("Path parameters with the following names were not documented: [a]");
	}

	@SnippetTest
	void missingPathParameter(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new PathParametersSnippet(Arrays.asList(parameterWithName("a").description("one")))
				.document(operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/")
					.build()))
			.withMessage("Path parameters with the following names were not found in the request: [a]");
	}

	@SnippetTest
	void undocumentedAndMissingPathParameters(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new PathParametersSnippet(Arrays.asList(parameterWithName("a").description("one")))
				.document(operationBuilder.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, "/{b}")
					.build()))
			.withMessage("Path parameters with the following names were not documented: [b]. Path parameters with the"
					+ " following names were not found in the request: [a]");
	}

	private String getTitle(TemplateFormat templateFormat) {
		return getTitle(templateFormat, "/{a}/{b}");
	}

	private String getTitle(TemplateFormat templateFormat, String title) {
		if (templateFormat.getId().equals(TemplateFormats.asciidoctor().getId())) {
			return "+" + title + "+";
		}
		return "`" + title + "`";
	}

}
