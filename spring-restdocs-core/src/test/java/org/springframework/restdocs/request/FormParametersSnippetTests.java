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

import org.springframework.restdocs.snippet.SnippetException;
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
 * Tests for {@link FormParametersSnippet}.
 *
 * @author Andy Wilkinson
 */
class FormParametersSnippetTests {

	@RenderedSnippetTest
	void formParameters(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new FormParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one"), parameterWithName("b").description("two")))
			.document(operationBuilder.request("http://localhost").content("a=alpha&b=bravo").build());
		assertThat(snippets.formParameters())
			.isTable((table) -> table.withHeader("Parameter", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void formParameterWithNoValue(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new FormParametersSnippet(Arrays.asList(parameterWithName("a").description("one")))
			.document(operationBuilder.request("http://localhost").content("a=").build());
		assertThat(snippets.formParameters())
			.isTable((table) -> table.withHeader("Parameter", "Description").row("`a`", "one"));
	}

	@RenderedSnippetTest
	void ignoredFormParameter(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new FormParametersSnippet(
				Arrays.asList(parameterWithName("a").ignored(), parameterWithName("b").description("two")))
			.document(operationBuilder.request("http://localhost").content("a=alpha&b=bravo").build());
		assertThat(snippets.formParameters())
			.isTable((table) -> table.withHeader("Parameter", "Description").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void allUndocumentedFormParametersCanBeIgnored(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new FormParametersSnippet(Arrays.asList(parameterWithName("b").description("two")), true)
			.document(operationBuilder.request("http://localhost").content("a=alpha&b=bravo").build());
		assertThat(snippets.formParameters())
			.isTable((table) -> table.withHeader("Parameter", "Description").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void missingOptionalFormParameter(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new FormParametersSnippet(Arrays.asList(parameterWithName("a").description("one").optional(),
				parameterWithName("b").description("two")))
			.document(operationBuilder.request("http://localhost").content("b=bravo").build());
		assertThat(snippets.formParameters())
			.isTable((table) -> table.withHeader("Parameter", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void presentOptionalFormParameter(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new FormParametersSnippet(Arrays.asList(parameterWithName("a").description("one").optional()))
			.document(operationBuilder.request("http://localhost").content("a=alpha").build());
		assertThat(snippets.formParameters())
			.isTable((table) -> table.withHeader("Parameter", "Description").row("`a`", "one"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "form-parameters", template = "form-parameters-with-title")
	void formParametersWithCustomAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new FormParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one").attributes(key("foo").value("alpha")),
						parameterWithName("b").description("two").attributes(key("foo").value("bravo"))),
				attributes(key("title").value("The title")))
			.document(operationBuilder.request("http://localhost").content("a=alpha&b=bravo").build());
		assertThat(snippets.formParameters()).contains("The title");
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "form-parameters", template = "form-parameters-with-extra-column")
	void formParametersWithCustomDescriptorAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new FormParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one").attributes(key("foo").value("alpha")),
						parameterWithName("b").description("two").attributes(key("foo").value("bravo"))))
			.document(operationBuilder.request("http://localhost").content("a=alpha&b=bravo").build());
		assertThat(snippets.formParameters()).isTable((table) -> table.withHeader("Parameter", "Description", "Foo")
			.row("a", "one", "alpha")
			.row("b", "two", "bravo"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "form-parameters", template = "form-parameters-with-optional-column")
	void formParametersWithOptionalColumn(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new FormParametersSnippet(Arrays.asList(parameterWithName("a").description("one").optional(),
				parameterWithName("b").description("two")))
			.document(operationBuilder.request("http://localhost").content("a=alpha&b=bravo").build());
		assertThat(snippets.formParameters())
			.isTable((table) -> table.withHeader("Parameter", "Optional", "Description")
				.row("a", "true", "one")
				.row("b", "false", "two"));
	}

	@RenderedSnippetTest
	void additionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		RequestDocumentation.formParameters(parameterWithName("a").description("one"))
			.and(parameterWithName("b").description("two"))
			.document(operationBuilder.request("http://localhost").content("a=alpha&b=bravo").build());
		assertThat(snippets.formParameters())
			.isTable((table) -> table.withHeader("Parameter", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void additionalDescriptorsWithRelaxedFormParameters(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		RequestDocumentation.relaxedFormParameters(parameterWithName("a").description("one"))
			.and(parameterWithName("b").description("two"))
			.document(operationBuilder.request("http://localhost").content("a=alpha&b=bravo&c=undocumented").build());
		assertThat(snippets.formParameters())
			.isTable((table) -> table.withHeader("Parameter", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void formParametersWithEscapedContent(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		RequestDocumentation.formParameters(parameterWithName("Foo|Bar").description("one|two"))
			.document(operationBuilder.request("http://localhost").content("Foo%7CBar=baz").build());
		assertThat(snippets.formParameters())
			.isTable((table) -> table.withHeader("Parameter", "Description").row("`Foo|Bar`", "one|two"));
	}

	@SnippetTest
	void undocumentedParameter(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new FormParametersSnippet(Collections.<ParameterDescriptor>emptyList())
				.document(operationBuilder.request("http://localhost").content("a=alpha").build()))
			.withMessage("Form parameters with the following names were not documented: [a]");
	}

	@SnippetTest
	void missingParameter(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new FormParametersSnippet(Arrays.asList(parameterWithName("a").description("one")))
				.document(operationBuilder.request("http://localhost").build()))
			.withMessage("Form parameters with the following names were not found in the request: [a]");
	}

	@SnippetTest
	void undocumentedAndMissingParameters(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new FormParametersSnippet(Arrays.asList(parameterWithName("a").description("one")))
				.document(operationBuilder.request("http://localhost").content("b=bravo").build()))
			.withMessage("Form parameters with the following names were not documented: [b]. Form parameters"
					+ " with the following names were not found in the request: [a]");
	}

}
