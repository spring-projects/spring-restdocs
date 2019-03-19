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
 * Tests for {@link RequestHeadersSnippet}.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 */
public class RequestHeadersSnippetTests extends AbstractSnippetTests {

	public RequestHeadersSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void requestWithHeaders() throws IOException {
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one"),
						headerWithName("Accept").description("two"),
						headerWithName("Accept-Encoding").description("three"),
						headerWithName("Accept-Language").description("four"),
						headerWithName("Cache-Control").description("five"),
						headerWithName("Connection").description("six"))).document(
								this.operationBuilder.request("http://localhost")
										.header("X-Test", "test").header("Accept", "*/*")
										.header("Accept-Encoding", "gzip, deflate")
										.header("Accept-Language", "en-US,en;q=0.5")
										.header("Cache-Control", "max-age=0")
										.header("Connection", "keep-alive").build());
		assertThat(this.generatedSnippets.requestHeaders())
				.is(tableWithHeader("Name", "Description").row("`X-Test`", "one")
						.row("`Accept`", "two").row("`Accept-Encoding`", "three")
						.row("`Accept-Language`", "four").row("`Cache-Control`", "five")
						.row("`Connection`", "six"));
	}

	@Test
	public void caseInsensitiveRequestHeaders() throws IOException {
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one")))
						.document(this.operationBuilder.request("/")
								.header("X-test", "test").build());
		assertThat(this.generatedSnippets.requestHeaders())
				.is(tableWithHeader("Name", "Description").row("`X-Test`", "one"));
	}

	@Test
	public void undocumentedRequestHeader() throws IOException {
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one")))
						.document(this.operationBuilder.request("http://localhost")
								.header("X-Test", "test").header("Accept", "*/*")
								.build());
		assertThat(this.generatedSnippets.requestHeaders())
				.is(tableWithHeader("Name", "Description").row("`X-Test`", "one"));
	}

	@Test
	public void requestHeadersWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-headers"))
				.willReturn(snippetResource("request-headers-with-title"));
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one")), attributes(
						key("title").value("Custom title")))
								.document(
										this.operationBuilder
												.attribute(TemplateEngine.class.getName(),
														new MustacheTemplateEngine(
																resolver))
												.request("http://localhost")
												.header("X-Test", "test").build());
		assertThat(this.generatedSnippets.requestHeaders()).contains("Custom title");
	}

	@Test
	public void requestHeadersWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-headers"))
				.willReturn(snippetResource("request-headers-with-extra-column"));
		new RequestHeadersSnippet(Arrays.asList(
				headerWithName("X-Test").description("one")
						.attributes(key("foo").value("alpha")),
				headerWithName("Accept-Encoding").description("two")
						.attributes(key("foo").value("bravo")),
				headerWithName("Accept").description("three")
						.attributes(key("foo").value("charlie"))))
								.document(
										this.operationBuilder
												.attribute(TemplateEngine.class.getName(),
														new MustacheTemplateEngine(
																resolver))
												.request("http://localhost")
												.header("X-Test", "test")
												.header("Accept-Encoding",
														"gzip, deflate")
												.header("Accept", "*/*").build());
		assertThat(this.generatedSnippets.requestHeaders()).is(//
				tableWithHeader("Name", "Description", "Foo")
						.row("X-Test", "one", "alpha")
						.row("Accept-Encoding", "two", "bravo")
						.row("Accept", "three", "charlie"));
	}

	@Test
	public void additionalDescriptors() throws IOException {
		HeaderDocumentation
				.requestHeaders(headerWithName("X-Test").description("one"),
						headerWithName("Accept").description("two"),
						headerWithName("Accept-Encoding").description("three"),
						headerWithName("Accept-Language").description("four"))
				.and(headerWithName("Cache-Control").description("five"),
						headerWithName("Connection").description("six"))
				.document(this.operationBuilder.request("http://localhost")
						.header("X-Test", "test").header("Accept", "*/*")
						.header("Accept-Encoding", "gzip, deflate")
						.header("Accept-Language", "en-US,en;q=0.5")
						.header("Cache-Control", "max-age=0")
						.header("Connection", "keep-alive").build());
		assertThat(this.generatedSnippets.requestHeaders())
				.is(tableWithHeader("Name", "Description").row("`X-Test`", "one")
						.row("`Accept`", "two").row("`Accept-Encoding`", "three")
						.row("`Accept-Language`", "four").row("`Cache-Control`", "five")
						.row("`Connection`", "six"));
	}

	@Test
	public void tableCellContentIsEscapedWhenNecessary() throws IOException {
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("Foo|Bar").description("one|two")))
						.document(this.operationBuilder.request("http://localhost")
								.header("Foo|Bar", "baz").build());
		assertThat(this.generatedSnippets.requestHeaders()).is(
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
