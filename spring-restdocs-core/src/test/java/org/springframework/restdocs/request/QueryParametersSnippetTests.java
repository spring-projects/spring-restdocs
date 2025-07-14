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
 * Tests for {@link QueryParametersSnippet}.
 *
 * @author Andy Wilkinson
 */
public class QueryParametersSnippetTests extends AbstractSnippetTests {

	public QueryParametersSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void queryParameters() throws IOException {
		new QueryParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one"), parameterWithName("b").description("two")))
			.document(this.operationBuilder.request("http://localhost?a=alpha&b=bravo").build());
		assertThat(this.generatedSnippets.queryParameters())
			.is(tableWithHeader("Parameter", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@Test
	public void queryParameterWithNoValue() throws IOException {
		new QueryParametersSnippet(Arrays.asList(parameterWithName("a").description("one")))
			.document(this.operationBuilder.request("http://localhost?a").build());
		assertThat(this.generatedSnippets.queryParameters())
			.is(tableWithHeader("Parameter", "Description").row("`a`", "one"));
	}

	@Test
	public void ignoredQueryParameter() throws IOException {
		new QueryParametersSnippet(
				Arrays.asList(parameterWithName("a").ignored(), parameterWithName("b").description("two")))
			.document(this.operationBuilder.request("http://localhost?a=alpha&b=bravo").build());
		assertThat(this.generatedSnippets.queryParameters())
			.is(tableWithHeader("Parameter", "Description").row("`b`", "two"));
	}

	@Test
	public void allUndocumentedQueryParametersCanBeIgnored() throws IOException {
		new QueryParametersSnippet(Arrays.asList(parameterWithName("b").description("two")), true)
			.document(this.operationBuilder.request("http://localhost?a=alpha&b=bravo").build());
		assertThat(this.generatedSnippets.queryParameters())
			.is(tableWithHeader("Parameter", "Description").row("`b`", "two"));
	}

	@Test
	public void missingOptionalQueryParameter() throws IOException {
		new QueryParametersSnippet(Arrays.asList(parameterWithName("a").description("one").optional(),
				parameterWithName("b").description("two")))
			.document(this.operationBuilder.request("http://localhost?b=bravo").build());
		assertThat(this.generatedSnippets.queryParameters())
			.is(tableWithHeader("Parameter", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@Test
	public void presentOptionalQueryParameter() throws IOException {
		new QueryParametersSnippet(Arrays.asList(parameterWithName("a").description("one").optional()))
			.document(this.operationBuilder.request("http://localhost?a=alpha").build());
		assertThat(this.generatedSnippets.queryParameters())
			.is(tableWithHeader("Parameter", "Description").row("`a`", "one"));
	}

	@Test
	public void queryParametersWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("query-parameters"))
			.willReturn(snippetResource("query-parameters-with-title"));
		new QueryParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one").attributes(key("foo").value("alpha")),
						parameterWithName("b").description("two").attributes(key("foo").value("bravo"))),
				attributes(key("title").value("The title")))
			.document(this.operationBuilder
				.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
				.request("http://localhost?a=alpha&b=bravo")
				.build());
		assertThat(this.generatedSnippets.queryParameters()).contains("The title");
	}

	@Test
	public void queryParametersWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("query-parameters"))
			.willReturn(snippetResource("query-parameters-with-extra-column"));
		new QueryParametersSnippet(
				Arrays.asList(parameterWithName("a").description("one").attributes(key("foo").value("alpha")),
						parameterWithName("b").description("two").attributes(key("foo").value("bravo"))))
			.document(this.operationBuilder
				.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
				.request("http://localhost?a=alpha&b=bravo")
				.build());
		assertThat(this.generatedSnippets.queryParameters())
			.is(tableWithHeader("Parameter", "Description", "Foo").row("a", "one", "alpha").row("b", "two", "bravo"));
	}

	@Test
	public void queryParametersWithOptionalColumn() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("query-parameters"))
			.willReturn(snippetResource("query-parameters-with-optional-column"));
		new QueryParametersSnippet(Arrays.asList(parameterWithName("a").description("one").optional(),
				parameterWithName("b").description("two")))
			.document(this.operationBuilder
				.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
				.request("http://localhost?a=alpha&b=bravo")
				.build());
		assertThat(this.generatedSnippets.queryParameters())
			.is(tableWithHeader("Parameter", "Optional", "Description").row("a", "true", "one")
				.row("b", "false", "two"));
	}

	@Test
	public void additionalDescriptors() throws IOException {
		RequestDocumentation.queryParameters(parameterWithName("a").description("one"))
			.and(parameterWithName("b").description("two"))
			.document(this.operationBuilder.request("http://localhost?a=alpha&b=bravo").build());
		assertThat(this.generatedSnippets.queryParameters())
			.is(tableWithHeader("Parameter", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@Test
	public void additionalDescriptorsWithRelaxedQueryParameters() throws IOException {
		RequestDocumentation.relaxedQueryParameters(parameterWithName("a").description("one"))
			.and(parameterWithName("b").description("two"))
			.document(this.operationBuilder.request("http://localhost?a=alpha&b=bravo&c=undocumented").build());
		assertThat(this.generatedSnippets.queryParameters())
			.is(tableWithHeader("Parameter", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@Test
	public void queryParametersWithEscapedContent() throws IOException {
		RequestDocumentation.queryParameters(parameterWithName("Foo|Bar").description("one|two"))
			.document(this.operationBuilder.request("http://localhost?Foo%7CBar=baz").build());
		assertThat(this.generatedSnippets.queryParameters()).is(tableWithHeader("Parameter", "Description")
			.row(escapeIfNecessary("`Foo|Bar`"), escapeIfNecessary("one|two")));
	}

	private String escapeIfNecessary(String input) {
		if (this.templateFormat.getId().equals(TemplateFormats.markdown().getId())) {
			return input;
		}
		return input.replace("|", "\\|");
	}

}
