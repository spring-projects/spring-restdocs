/*
 * Copyright 2014-2022 the original author or authors.
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
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link RequestCookiesSnippet}.
 *
 * @author Clyde Stubbs
 * @author Andy Wilkinson
 */
public class RequestCookiesSnippetTests extends AbstractSnippetTests {

	public RequestCookiesSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void requestWithCookies() throws IOException {
		new RequestCookiesSnippet(
				Arrays.asList(cookieWithName("tz").description("one"), cookieWithName("logged_in").description("two")))
						.document(this.operationBuilder.request("http://localhost").cookie("tz", "Europe%2FLondon")
								.cookie("logged_in", "true").build());
		assertThat(this.generatedSnippets.requestCookies())
				.is(tableWithHeader("Name", "Description").row("`tz`", "one").row("`logged_in`", "two"));
	}

	@Test
	public void ignoredRequestCookie() throws IOException {
		new RequestCookiesSnippet(
				Arrays.asList(cookieWithName("tz").ignored(), cookieWithName("logged_in").description("two")))
						.document(this.operationBuilder.request("http://localhost").cookie("tz", "Europe%2FLondon")
								.cookie("logged_in", "true").build());
		assertThat(this.generatedSnippets.requestCookies())
				.is(tableWithHeader("Name", "Description").row("`logged_in`", "two"));
	}

	@Test
	public void allUndocumentedCookiesCanBeIgnored() throws IOException {
		new RequestCookiesSnippet(
				Arrays.asList(cookieWithName("tz").description("one"), cookieWithName("logged_in").description("two")),
				true).document(
						this.operationBuilder.request("http://localhost").cookie("tz", "Europe%2FLondon")
								.cookie("logged_in", "true").cookie("user_session", "abcd1234efgh5678").build());
		assertThat(this.generatedSnippets.requestCookies())
				.is(tableWithHeader("Name", "Description").row("`tz`", "one").row("`logged_in`", "two"));
	}

	@Test
	public void missingOptionalCookie() throws IOException {
		new RequestCookiesSnippet(Arrays.asList(cookieWithName("tz").description("one").optional(),
				cookieWithName("logged_in").description("two"))).document(
						this.operationBuilder.request("http://localhost").cookie("logged_in", "true").build());
		assertThat(this.generatedSnippets.requestCookies())
				.is(tableWithHeader("Name", "Description").row("`tz`", "one").row("`logged_in`", "two"));
	}

	@Test
	public void requestCookiesWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-cookies"))
				.willReturn(snippetResource("request-cookies-with-title"));
		new RequestCookiesSnippet(Collections.singletonList(cookieWithName("tz").description("one")),
				attributes(key("title").value("Custom title")))
						.document(this.operationBuilder
								.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
								.request("http://localhost").cookie("tz", "Europe%2FLondon").build());
		assertThat(this.generatedSnippets.requestCookies()).contains("Custom title");
	}

	@Test
	public void requestCookiesWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-cookies"))
				.willReturn(snippetResource("request-cookies-with-extra-column"));
		new RequestCookiesSnippet(
				Arrays.asList(cookieWithName("tz").description("one").attributes(key("foo").value("alpha")),
						cookieWithName("logged_in").description("two").attributes(key("foo").value("bravo"))))
								.document(this.operationBuilder
										.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
										.request("http://localhost").cookie("tz", "Europe%2FLondon")
										.cookie("logged_in", "true").build());
		assertThat(this.generatedSnippets.requestCookies()).is(//
				tableWithHeader("Name", "Description", "Foo").row("tz", "one", "alpha").row("logged_in", "two",
						"bravo"));
	}

	@Test
	public void additionalDescriptors() throws IOException {
		new RequestCookiesSnippet(
				Arrays.asList(cookieWithName("tz").description("one"), cookieWithName("logged_in").description("two")))
						.and(cookieWithName("user_session").description("three"))
						.document(this.operationBuilder.request("http://localhost").cookie("tz", "Europe%2FLondon")
								.cookie("logged_in", "true").cookie("user_session", "abcd1234efgh5678").build());
		assertThat(this.generatedSnippets.requestCookies()).is(tableWithHeader("Name", "Description").row("`tz`", "one")
				.row("`logged_in`", "two").row("`user_session`", "three"));
	}

	@Test
	public void additionalDescriptorsWithRelaxedRequestCookies() throws IOException {
		new RequestCookiesSnippet(
				Arrays.asList(cookieWithName("tz").description("one"), cookieWithName("logged_in").description("two")),
				true).and(cookieWithName("user_session").description("three"))
						.document(this.operationBuilder.request("http://localhost").cookie("tz", "Europe%2FLondon")
								.cookie("logged_in", "true").cookie("user_session", "abcd1234efgh5678")
								.cookie("color_theme", "light").build());
		assertThat(this.generatedSnippets.requestCookies()).is(tableWithHeader("Name", "Description").row("`tz`", "one")
				.row("`logged_in`", "two").row("`user_session`", "three"));
	}

	@Test
	public void tableCellContentIsEscapedWhenNecessary() throws IOException {
		new RequestCookiesSnippet(Collections.singletonList(cookieWithName("Foo|Bar").description("one|two")))
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
