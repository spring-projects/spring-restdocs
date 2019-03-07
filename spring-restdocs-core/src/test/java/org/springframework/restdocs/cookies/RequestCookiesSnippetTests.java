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
 * Tests for {@link RequestCookiesSnippet}.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 * @author Clyde Stubbs
 */
public class RequestCookiesSnippetTests extends AbstractSnippetTests {

	public RequestCookiesSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void requestWithCookies() throws IOException {
		new RequestCookiesSnippet(Arrays.asList(CookieDocumentation.cookieWithName("Session").description("one"),
				CookieDocumentation.cookieWithName("User").description("two"),
				CookieDocumentation.cookieWithName("Timeout").description("three"),
				CookieDocumentation.cookieWithName("Preference").description("four"),
				CookieDocumentation.cookieWithName("Connection").description("five")))
						.document(this.operationBuilder.request("http://localhost").cookie("Session", "test")
								.cookie("User", "nobody").cookie("Timeout", "3600").cookie("Preference", "inverse")
								.cookie("Connection", "secure").build());
		assertThat(this.generatedSnippets.requestCookies())
				.is(tableWithHeader("Name", "Description").row("`Session`", "one").row("`User`", "two")
						.row("`Timeout`", "three").row("`Preference`", "four").row("`Connection`", "five"));
	}

	@Test
	public void undocumentedRequestCookie() throws IOException {
		new RequestCookiesSnippet(
				Collections.singletonList(CookieDocumentation.cookieWithName("X-Test").description("one")))
						.document(this.operationBuilder.request("http://localhost").cookie("X-Test", "test")
								.cookie("Second", "*/*").build());
		assertThat(this.generatedSnippets.requestCookies())
				.is(tableWithHeader("Name", "Description").row("`X-Test`", "one"));
	}

	@Test
	public void requestCookiesWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-cookies"))
				.willReturn(snippetResource("request-cookies-with-title"));
		new RequestCookiesSnippet(
				Collections.singletonList(CookieDocumentation.cookieWithName("X-Test").description("one")),
				attributes(key("title").value("Custom title")))
						.document(this.operationBuilder
								.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
								.request("http://localhost").cookie("X-Test", "test").build());
		assertThat(this.generatedSnippets.requestCookies()).contains("Custom title");
	}

	@Test
	public void requestCookiesWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-cookies"))
				.willReturn(snippetResource("request-cookies-with-extra-column"));
		new RequestCookiesSnippet(Arrays.asList(
				CookieDocumentation.cookieWithName("X-Test").description("one").attributes(key("foo").value("alpha")),
				CookieDocumentation.cookieWithName("Accept-Encoding").description("two")
						.attributes(key("foo").value("bravo")),
				CookieDocumentation.cookieWithName("Accept").description("three")
						.attributes(key("foo").value("charlie"))))
								.document(this.operationBuilder
										.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
										.request("http://localhost").cookie("X-Test", "test")
										.cookie("Accept-Encoding", "gzip, deflate").cookie("Accept", "*/*").build());
		assertThat(this.generatedSnippets.requestCookies()).is(//
				tableWithHeader("Name", "Description", "Foo").row("X-Test", "one", "alpha")
						.row("Accept-Encoding", "two", "bravo").row("Accept", "three", "charlie"));
	}

	@Test
	public void additionalDescriptors() throws IOException {
		CookieDocumentation
				.requestCookies(CookieDocumentation.cookieWithName("X-Test").description("one"),
						CookieDocumentation.cookieWithName("Accept").description("two"),
						CookieDocumentation.cookieWithName("Accept-Encoding").description("three"),
						CookieDocumentation.cookieWithName("Accept-Language").description("four"))
				.and(CookieDocumentation.cookieWithName("Cache-Control").description("five"),
						CookieDocumentation.cookieWithName("Connection").description("six"))
				.document(this.operationBuilder.request("http://localhost").cookie("X-Test", "test")
						.cookie("Accept", "*/*").cookie("Accept-Encoding", "gzip, deflate")
						.cookie("Accept-Language", "en-US,en;q=0.5").cookie("Cache-Control", "max-age=0")
						.cookie("Connection", "keep-alive").build());
		assertThat(this.generatedSnippets.requestCookies()).is(tableWithHeader("Name", "Description")
				.row("`X-Test`", "one").row("`Accept`", "two").row("`Accept-Encoding`", "three")
				.row("`Accept-Language`", "four").row("`Cache-Control`", "five").row("`Connection`", "six"));
	}

	@Test
	public void tableCellContentIsEscapedWhenNecessary() throws IOException {
		new RequestCookiesSnippet(
				Collections.singletonList(CookieDocumentation.cookieWithName("Foo|Bar").description("one|two")))
						.document(this.operationBuilder.request("http://localhost").cookie("Foo|Bar", "baz").build());
		assertThat(this.generatedSnippets.requestCookies()).is(tableWithHeader("Name", "Description")
				.row(escapeIfNecessary("`Foo|Bar`"), escapeIfNecessary("one|two")));
	}

	private String escapeIfNecessary(String input) {
		if (this.templateFormat.getId().equals(TemplateFormats.markdown().getId())) {
			return input;
		}
		return input.replace("|", "\\|");
	}

}
