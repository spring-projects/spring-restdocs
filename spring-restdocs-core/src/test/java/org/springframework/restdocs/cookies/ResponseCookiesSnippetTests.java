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

package org.springframework.restdocs.cookies;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

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
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link ResponseCookiesSnippet}.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 * @author Clyde Stubbs
 */
public class ResponseCookiesSnippetTests extends AbstractSnippetTests {

	public ResponseCookiesSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void responseWithCookies() throws IOException {
		new ResponseCookiesSnippet(Arrays.asList(CookieDocumentation.cookieWithName("X-Test").description("one"),
				CookieDocumentation.cookieWithName("Content-Type").description("two"),
				CookieDocumentation.cookieWithName("Etag").description("three"),
				CookieDocumentation.cookieWithName("Cache-Control").description("five"),
				CookieDocumentation.cookieWithName("Vary").description("six")))
						.document(this.operationBuilder.response().cookie("X-Test", "test")
								.cookie("Content-Type", "application/json").cookie("Etag", "lskjadldj3ii32l2ij23")
								.cookie("Cache-Control", "max-age=0").cookie("Vary", "User-Agent").build());
		assertThat(this.generatedSnippets.responseCookies())
				.is(tableWithHeader("Name", "Description").row("`X-Test`", "one").row("`Content-Type`", "two")
						.row("`Etag`", "three").row("`Cache-Control`", "five").row("`Vary`", "six"));
	}

	@Test
	public void undocumentedResponseCookie() throws IOException {
		new ResponseCookiesSnippet(
				Collections.singletonList(CookieDocumentation.cookieWithName("X-Test").description("one")))
						.document(this.operationBuilder.response().cookie("X-Test", "test")
								.cookie("Content-Type", "*/*").build());
		assertThat(this.generatedSnippets.responseCookies())
				.is(tableWithHeader("Name", "Description").row("`X-Test`", "one"));
	}

	@Test
	public void responseCookiesWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-cookies"))
				.willReturn(snippetResource("response-cookies-with-title"));
		new ResponseCookiesSnippet(
				Collections.singletonList(CookieDocumentation.cookieWithName("X-Test").description("one")),
				attributes(key("title").value("Custom title")))
						.document(this.operationBuilder
								.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
								.response().cookie("X-Test", "test").build());
		assertThat(this.generatedSnippets.responseCookies()).contains("Custom title");
	}

	@Test
	public void responseCookiesWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-cookies"))
				.willReturn(snippetResource("response-cookies-with-extra-column"));
		new ResponseCookiesSnippet(Arrays.asList(
				CookieDocumentation.cookieWithName("X-Test").description("one").attributes(key("foo").value("alpha")),
				CookieDocumentation.cookieWithName("Content-Type").description("two")
						.attributes(key("foo").value("bravo")),
				CookieDocumentation.cookieWithName("Etag").description("three")
						.attributes(key("foo").value("charlie"))))
								.document(this.operationBuilder
										.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
										.response().cookie("X-Test", "test").cookie("Content-Type", "application/json")
										.cookie("Etag", "lskjadldj3ii32l2ij23").build());
		assertThat(this.generatedSnippets.responseCookies()).is(tableWithHeader("Name", "Description", "Foo")
				.row("X-Test", "one", "alpha").row("Content-Type", "two", "bravo").row("Etag", "three", "charlie"));
	}

	@Test
	public void additionalDescriptors() throws IOException {
		CookieDocumentation
				.responseCookies(CookieDocumentation.cookieWithName("X-Test").description("one"),
						CookieDocumentation.cookieWithName("Content-Type").description("two"),
						CookieDocumentation.cookieWithName("Etag").description("three"))
				.and(CookieDocumentation.cookieWithName("Cache-Control").description("five"),
						CookieDocumentation.cookieWithName("Vary").description("six"))
				.document(this.operationBuilder.response().cookie("X-Test", "test")
						.cookie("Content-Type", "application/json").cookie("Etag", "lskjadldj3ii32l2ij23")
						.cookie("Cache-Control", "max-age=0").cookie("Vary", "User-Agent").build());
		assertThat(this.generatedSnippets.responseCookies())
				.is(tableWithHeader("Name", "Description").row("`X-Test`", "one").row("`Content-Type`", "two")
						.row("`Etag`", "three").row("`Cache-Control`", "five").row("`Vary`", "six"));
	}

	@Test
	public void tableCellContentIsEscapedWhenNecessary() throws IOException {
		new ResponseCookiesSnippet(
				Collections.singletonList(CookieDocumentation.cookieWithName("Foo|Bar").description("one|two")))
						.document(this.operationBuilder.response().cookie("Foo|Bar", "baz").build());
		assertThat(this.generatedSnippets.responseCookies()).is(tableWithHeader("Name", "Description")
				.row(escapeIfNecessary("`Foo|Bar`"), escapeIfNecessary("one|two")));
	}

	private String escapeIfNecessary(String input) {
		if (this.templateFormat.getId().equals(TemplateFormats.markdown().getId())) {
			return input;
		}
		return input.replace("|", "\\|");
	}

}
