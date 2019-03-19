/*
 * Copyright 2014-2018 the original author or authors.
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

package org.springframework.restdocs.headers;

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
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link ResponseHeadersSnippet}.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 */
public class ResponseHeadersSnippetTests extends AbstractSnippetTests {

	public ResponseHeadersSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void responseWithHeaders() throws IOException {
		new ResponseHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one"),
						headerWithName("Content-Type").description("two"),
						headerWithName("Etag").description("three"),
						headerWithName("Cache-Control").description("five"),
						headerWithName("Vary").description("six"))).document(
								this.operationBuilder.response().header("X-Test", "test")
										.header("Content-Type", "application/json")
										.header("Etag", "lskjadldj3ii32l2ij23")
										.header("Cache-Control", "max-age=0")
										.header("Vary", "User-Agent").build());
		assertThat(this.generatedSnippets.responseHeaders())
				.is(tableWithHeader("Name", "Description").row("`X-Test`", "one")
						.row("`Content-Type`", "two").row("`Etag`", "three")
						.row("`Cache-Control`", "five").row("`Vary`", "six"));
	}

	@Test
	public void caseInsensitiveResponseHeaders() throws IOException {
		new ResponseHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one")))
						.document(this.operationBuilder.response()
								.header("X-test", "test").build());
		assertThat(this.generatedSnippets.responseHeaders())
				.is(tableWithHeader("Name", "Description").row("`X-Test`", "one"));
	}

	@Test
	public void undocumentedResponseHeader() throws IOException {
		new ResponseHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one"))).document(
						this.operationBuilder.response().header("X-Test", "test")
								.header("Content-Type", "*/*").build());
		assertThat(this.generatedSnippets.responseHeaders())
				.is(tableWithHeader("Name", "Description").row("`X-Test`", "one"));
	}

	@Test
	public void responseHeadersWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-headers"))
				.willReturn(snippetResource("response-headers-with-title"));
		new ResponseHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one")), attributes(
						key("title").value("Custom title")))
								.document(
										this.operationBuilder
												.attribute(TemplateEngine.class.getName(),
														new MustacheTemplateEngine(
																resolver))
												.response().header("X-Test", "test")
												.build());
		assertThat(this.generatedSnippets.responseHeaders()).contains("Custom title");
	}

	@Test
	public void responseHeadersWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-headers"))
				.willReturn(snippetResource("response-headers-with-extra-column"));
		new ResponseHeadersSnippet(Arrays.asList(
				headerWithName("X-Test").description("one")
						.attributes(key("foo").value("alpha")),
				headerWithName("Content-Type").description("two")
						.attributes(key("foo").value("bravo")),
				headerWithName("Etag").description("three")
						.attributes(key("foo").value("charlie"))))
								.document(
										this.operationBuilder
												.attribute(TemplateEngine.class.getName(),
														new MustacheTemplateEngine(
																resolver))
												.response().header("X-Test", "test")
												.header("Content-Type",
														"application/json")
												.header("Etag", "lskjadldj3ii32l2ij23")
												.build());
		assertThat(this.generatedSnippets.responseHeaders())
				.is(tableWithHeader("Name", "Description", "Foo")
						.row("X-Test", "one", "alpha").row("Content-Type", "two", "bravo")
						.row("Etag", "three", "charlie"));
	}

	@Test
	public void additionalDescriptors() throws IOException {
		HeaderDocumentation
				.responseHeaders(headerWithName("X-Test").description("one"),
						headerWithName("Content-Type").description("two"),
						headerWithName("Etag").description("three"))
				.and(headerWithName("Cache-Control").description("five"),
						headerWithName("Vary").description("six"))
				.document(this.operationBuilder.response().header("X-Test", "test")
						.header("Content-Type", "application/json")
						.header("Etag", "lskjadldj3ii32l2ij23")
						.header("Cache-Control", "max-age=0").header("Vary", "User-Agent")
						.build());
		assertThat(this.generatedSnippets.responseHeaders())
				.is(tableWithHeader("Name", "Description").row("`X-Test`", "one")
						.row("`Content-Type`", "two").row("`Etag`", "three")
						.row("`Cache-Control`", "five").row("`Vary`", "six"));
	}

	@Test
	public void tableCellContentIsEscapedWhenNecessary() throws IOException {
		new ResponseHeadersSnippet(
				Arrays.asList(headerWithName("Foo|Bar").description("one|two")))
						.document(this.operationBuilder.response()
								.header("Foo|Bar", "baz").build());
		assertThat(this.generatedSnippets.responseHeaders()).is(
				tableWithHeader("Name", "Description").row(escapeIfNecessary("`Foo|Bar`"),
						escapeIfNecessary("one|two")));
	}

	private String escapeIfNecessary(String input) {
		if (this.templateFormat.getId().equals(TemplateFormats.markdown().getId())) {
			return input;
		}
		return input.replace("|", "\\|");
	}

}
