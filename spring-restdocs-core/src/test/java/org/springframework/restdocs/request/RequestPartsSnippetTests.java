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
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link RequestPartsSnippet}.
 *
 * @author Andy Wilkinson
 */
public class RequestPartsSnippetTests extends AbstractSnippetTests {

	public RequestPartsSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void requestParts() throws IOException {
		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one"), partWithName("b").description("two")))
			.document(this.operationBuilder.request("http://localhost")
				.part("a", "bravo".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(this.generatedSnippets.requestParts())
			.is(tableWithHeader("Part", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@Test
	public void ignoredRequestPart() throws IOException {
		new RequestPartsSnippet(Arrays.asList(partWithName("a").ignored(), partWithName("b").description("two")))
			.document(this.operationBuilder.request("http://localhost")
				.part("a", "bravo".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(this.generatedSnippets.requestParts()).is(tableWithHeader("Part", "Description").row("`b`", "two"));
	}

	@Test
	public void allUndocumentedRequestPartsCanBeIgnored() throws IOException {
		new RequestPartsSnippet(Arrays.asList(partWithName("b").description("two")), true)
			.document(this.operationBuilder.request("http://localhost")
				.part("a", "bravo".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(this.generatedSnippets.requestParts()).is(tableWithHeader("Part", "Description").row("`b`", "two"));
	}

	@Test
	public void missingOptionalRequestPart() throws IOException {
		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one").optional(), partWithName("b").description("two")))
			.document(this.operationBuilder.request("http://localhost").part("b", "bravo".getBytes()).build());
		assertThat(this.generatedSnippets.requestParts())
			.is(tableWithHeader("Part", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@Test
	public void presentOptionalRequestPart() throws IOException {
		new RequestPartsSnippet(Arrays.asList(partWithName("a").description("one").optional()))
			.document(this.operationBuilder.request("http://localhost").part("a", "one".getBytes()).build());
		assertThat(this.generatedSnippets.requestParts()).is(tableWithHeader("Part", "Description").row("`a`", "one"));
	}

	@Test
	public void requestPartsWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-parts"))
			.willReturn(snippetResource("request-parts-with-title"));
		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one").attributes(key("foo").value("alpha")),
						partWithName("b").description("two").attributes(key("foo").value("bravo"))),
				attributes(key("title").value("The title")))
			.document(this.operationBuilder
				.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
				.request("http://localhost")
				.part("a", "alpha".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(this.generatedSnippets.requestParts()).contains("The title");
	}

	@Test
	public void requestPartsWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-parts"))
			.willReturn(snippetResource("request-parts-with-extra-column"));
		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one").attributes(key("foo").value("alpha")),
						partWithName("b").description("two").attributes(key("foo").value("bravo"))))
			.document(this.operationBuilder
				.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
				.request("http://localhost")
				.part("a", "alpha".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(this.generatedSnippets.requestParts())
			.is(tableWithHeader("Part", "Description", "Foo").row("a", "one", "alpha").row("b", "two", "bravo"));
	}

	@Test
	public void requestPartsWithOptionalColumn() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-parts"))
			.willReturn(snippetResource("request-parts-with-optional-column"));
		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one").optional(), partWithName("b").description("two")))
			.document(this.operationBuilder
				.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
				.request("http://localhost")
				.part("a", "alpha".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(this.generatedSnippets.requestParts())
			.is(tableWithHeader("Part", "Optional", "Description").row("a", "true", "one").row("b", "false", "two"));
	}

	@Test
	public void additionalDescriptors() throws IOException {
		RequestDocumentation.requestParts(partWithName("a").description("one"))
			.and(partWithName("b").description("two"))
			.document(this.operationBuilder.request("http://localhost")
				.part("a", "bravo".getBytes())
				.and()
				.part("b", "bravo".getBytes())
				.build());
		assertThat(this.generatedSnippets.requestParts())
			.is(tableWithHeader("Part", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@Test
	public void requestPartsWithEscapedContent() throws IOException {
		RequestDocumentation.requestParts(partWithName("Foo|Bar").description("one|two"))
			.document(this.operationBuilder.request("http://localhost").part("Foo|Bar", "baz".getBytes()).build());
		assertThat(this.generatedSnippets.requestParts()).is(tableWithHeader("Part", "Description")
			.row(escapeIfNecessary("`Foo|Bar`"), escapeIfNecessary("one|two")));
	}

	private String escapeIfNecessary(String input) {
		if (this.templateFormat.getId().equals(TemplateFormats.markdown().getId())) {
			return input;
		}
		return input.replace("|", "\\|");
	}

}
