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
 * Tests for {@link ResponseCookiesSnippet}.
 *
 * @author Clyde Stubbs
 * @author Andy Wilkinson
 */
public class ResponseCookiesSnippetTests extends AbstractSnippetTests {

	public ResponseCookiesSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void responseWithCookies() throws IOException {
		new ResponseCookiesSnippet(Arrays.asList(cookieWithName("has_recent_activity").description("one"),
				cookieWithName("user_session").description("two")))
			.document(this.operationBuilder.response()
				.cookie("has_recent_activity", "true")
				.cookie("user_session", "1234abcd5678efgh")
				.build());
		assertThat(this.generatedSnippets.responseCookies())
			.is(tableWithHeader("Name", "Description").row("`has_recent_activity`", "one")
				.row("`user_session`", "two"));
	}

	@Test
	public void ignoredResponseCookie() throws IOException {
		new ResponseCookiesSnippet(Arrays.asList(cookieWithName("has_recent_activity").ignored(),
				cookieWithName("user_session").description("two")))
			.document(this.operationBuilder.response()
				.cookie("has_recent_activity", "true")
				.cookie("user_session", "1234abcd5678efgh")
				.build());
		assertThat(this.generatedSnippets.responseCookies())
			.is(tableWithHeader("Name", "Description").row("`user_session`", "two"));
	}

	@Test
	public void allUndocumentedResponseCookiesCanBeIgnored() throws IOException {
		new ResponseCookiesSnippet(Arrays.asList(cookieWithName("has_recent_activity").description("one"),
				cookieWithName("user_session").description("two")), true)
			.document(this.operationBuilder.response()
				.cookie("has_recent_activity", "true")
				.cookie("user_session", "1234abcd5678efgh")
				.cookie("some_cookie", "value")
				.build());
		assertThat(this.generatedSnippets.responseCookies())
			.is(tableWithHeader("Name", "Description").row("`has_recent_activity`", "one")
				.row("`user_session`", "two"));
	}

	@Test
	public void missingOptionalResponseCookie() throws IOException {
		new ResponseCookiesSnippet(Arrays.asList(cookieWithName("has_recent_activity").description("one").optional(),
				cookieWithName("user_session").description("two")))
			.document(this.operationBuilder.response().cookie("user_session", "1234abcd5678efgh").build());
		assertThat(this.generatedSnippets.responseCookies())
			.is(tableWithHeader("Name", "Description").row("`has_recent_activity`", "one")
				.row("`user_session`", "two"));
	}

	@Test
	public void responseCookiesWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-cookies"))
			.willReturn(snippetResource("response-cookies-with-title"));
		new ResponseCookiesSnippet(Collections.singletonList(cookieWithName("has_recent_activity").description("one")),
				attributes(key("title").value("Custom title")))
			.document(this.operationBuilder
				.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
				.response()
				.cookie("has_recent_activity", "true")
				.build());
		assertThat(this.generatedSnippets.responseCookies()).contains("Custom title");
	}

	@Test
	public void responseCookiesWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-cookies"))
			.willReturn(snippetResource("response-cookies-with-extra-column"));
		new ResponseCookiesSnippet(Arrays.asList(
				cookieWithName("has_recent_activity").description("one").attributes(key("foo").value("alpha")),
				cookieWithName("user_session").description("two").attributes(key("foo").value("bravo")),
				cookieWithName("color_theme").description("three").attributes(key("foo").value("charlie"))))
			.document(this.operationBuilder
				.attribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
				.response()
				.cookie("has_recent_activity", "true")
				.cookie("user_session", "1234abcd5678efgh")
				.cookie("color_theme", "high_contrast")
				.build());
		assertThat(this.generatedSnippets.responseCookies())
			.is(tableWithHeader("Name", "Description", "Foo").row("has_recent_activity", "one", "alpha")
				.row("user_session", "two", "bravo")
				.row("color_theme", "three", "charlie"));
	}

	@Test
	public void additionalDescriptors() throws IOException {
		CookieDocumentation
			.responseCookies(cookieWithName("has_recent_activity").description("one"),
					cookieWithName("user_session").description("two"))
			.and(cookieWithName("color_theme").description("three"))
			.document(this.operationBuilder.response()
				.cookie("has_recent_activity", "true")
				.cookie("user_session", "1234abcd5678efgh")
				.cookie("color_theme", "light")
				.build());
		assertThat(this.generatedSnippets.responseCookies())
			.is(tableWithHeader("Name", "Description").row("`has_recent_activity`", "one")
				.row("`user_session`", "two")
				.row("`color_theme`", "three"));
	}

	@Test
	public void additionalDescriptorsWithRelaxedResponseCookies() throws IOException {
		CookieDocumentation.relaxedResponseCookies(cookieWithName("has_recent_activity").description("one"))
			.and(cookieWithName("color_theme").description("two"))
			.document(this.operationBuilder.response()
				.cookie("has_recent_activity", "true")
				.cookie("user_session", "1234abcd5678efgh")
				.cookie("color_theme", "light")
				.build());
		assertThat(this.generatedSnippets.responseCookies())
			.is(tableWithHeader("Name", "Description").row("`has_recent_activity`", "one").row("`color_theme`", "two"));
	}

	@Test
	public void tableCellContentIsEscapedWhenNecessary() throws IOException {
		new ResponseCookiesSnippet(Collections.singletonList(cookieWithName("Foo|Bar").description("one|two")))
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
