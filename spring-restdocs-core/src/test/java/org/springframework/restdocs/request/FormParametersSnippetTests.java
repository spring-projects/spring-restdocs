/*
 * Copyright 2014-2023 the original author or authors.
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

import org.junit.Test;

import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link FormParametersSnippet}.
 *
 * @author Andy Wilkinson
 */
public class FormParametersSnippetTests extends AbstractSnippetTests {

	public FormParametersSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void formParameters() throws IOException {
		new FormParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one"), parameterWithName("b").description("two")))
			.document(this.operationBuilder.request("http://localhost").content("a=alpha&b=bravo").build());
		assertThat(this.generatedSnippets.formParameters())
			.is(tableWithHeader("Parameter", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@Test
	public void formParameterWithNoValue() throws IOException {
		new FormParametersSnippet(Arrays.asList(parameterWithName("a").description("one")))
			.document(this.operationBuilder.request("http://localhost").content("a=").build());
		assertThat(this.generatedSnippets.formParameters())
			.is(tableWithHeader("Parameter", "Description").row("`a`", "one"));
	}

	@Test
	public void ignoredFormParameter() throws IOException {
		new FormParametersSnippet(
				Arrays.asList(parameterWithName("a").ignored(), parameterWithName("b").description("two")))
			.document(this.operationBuilder.request("http://localhost").content("a=alpha&b=bravo").build());
		assertThat(this.generatedSnippets.formParameters())
			.is(tableWithHeader("Parameter", "Description").row("`b`", "two"));
	}

	@Test
	public void allUndocumentedFormParametersCanBeIgnored() throws IOException {
		new FormParametersSnippet(Arrays.asList(parameterWithName("b").description("two")), true)
			.document(this.operationBuilder.request("http://localhost").content("a=alpha&b=bravo").build());
		assertThat(this.generatedSnippets.formParameters())
			.is(tableWithHeader("Parameter", "Description").row("`b`", "two"));
	}

	@Test
	public void missingOptionalFormParameter() throws IOException {
		new FormParametersSnippet(Arrays.asList(parameterWithName("a").description("one").optional(),
				parameterWithName("b").description("two")))
			.document(this.operationBuilder.request("http://localhost").content("b=bravo").build());
		assertThat(this.generatedSnippets.formParameters())
			.is(tableWithHeader("Parameter", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@Test
	public void presentOptionalFormParameter() throws IOException {
		new FormParametersSnippet(Arrays.asList(parameterWithName("a").description("one").optional()))
			.document(this.operationBuilder.request("http://localhost").content("a=alpha").build());
		assertThat(this.generatedSnippets.formParameters())
			.is(tableWithHeader("Parameter", "Description").row("`a`", "one"));
	}

	@Test
	public void formParametersWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("form-parameters"))
			.willReturn(snippetResource("form-parameters-with-title"));
		new FormParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one").attributes(key("foo").value("alpha")),
						parameterWithName("b").description("two").attributes(key("foo").value("bravo"))),
				attributes(key("title").value("The title")))
			.document(this.operationBuilder
				.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
				.request("http://localhost")
				.content("a=alpha&b=bravo")
				.build());
		assertThat(this.generatedSnippets.formParameters()).contains("The title");
	}

	@Test
	public void formParametersWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("form-parameters"))
			.willReturn(snippetResource("form-parameters-with-extra-column"));
		new FormParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one").attributes(key("foo").value("alpha")),
						parameterWithName("b").description("two").attributes(key("foo").value("bravo"))))
			.document(this.operationBuilder
				.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
				.request("http://localhost")
				.content("a=alpha&b=bravo")
				.build());
		assertThat(this.generatedSnippets.formParameters())
			.is(tableWithHeader("Parameter", "Description", "Foo").row("a", "one", "alpha").row("b", "two", "bravo"));
	}

	@Test
	public void formParametersWithOptionalColumn() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("form-parameters"))
			.willReturn(snippetResource("form-parameters-with-optional-column"));
		new FormParametersSnippet(Arrays.asList(parameterWithName("a").description("one").optional(),
				parameterWithName("b").description("two")))
			.document(this.operationBuilder
				.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
				.request("http://localhost")
				.content("a=alpha&b=bravo")
				.build());
		assertThat(this.generatedSnippets.formParameters())
			.is(tableWithHeader("Parameter", "Optional", "Description").row("a", "true", "one")
				.row("b", "false", "two"));
	}

	@Test
	public void additionalDescriptors() throws IOException {
		RequestDocumentation.formParameters(parameterWithName("a").description("one"))
			.and(parameterWithName("b").description("two"))
			.document(this.operationBuilder.request("http://localhost").content("a=alpha&b=bravo").build());
		assertThat(this.generatedSnippets.formParameters())
			.is(tableWithHeader("Parameter", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@Test
	public void additionalDescriptorsWithRelaxedFormParameters() throws IOException {
		RequestDocumentation.relaxedFormParameters(parameterWithName("a").description("one"))
			.and(parameterWithName("b").description("two"))
			.document(this.operationBuilder.request("http://localhost")
				.content("a=alpha&b=bravo&c=undocumented")
				.build());
		assertThat(this.generatedSnippets.formParameters())
			.is(tableWithHeader("Parameter", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@Test
	public void formParametersWithEscapedContent() throws IOException {
		RequestDocumentation.formParameters(parameterWithName("Foo|Bar").description("one|two"))
			.document(this.operationBuilder.request("http://localhost").content("Foo%7CBar=baz").build());
		assertThat(this.generatedSnippets.formParameters()).is(tableWithHeader("Parameter", "Description")
			.row(escapeIfNecessary("`Foo|Bar`"), escapeIfNecessary("one|two")));
	}

	private String escapeIfNecessary(String input) {
		if (this.templateFormat.getId().equals(TemplateFormats.markdown().getId())) {
			return input;
		}
		return input.replace("|", "\\|");
	}

}
