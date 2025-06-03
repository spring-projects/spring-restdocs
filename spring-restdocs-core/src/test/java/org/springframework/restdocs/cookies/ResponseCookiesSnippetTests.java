/*
 * Copyright 2014-2025 the original author or authors.
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

import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link ResponseCookiesSnippet}.
 *
 * @author Clyde Stubbs
 * @author Andy Wilkinson
 */
class ResponseCookiesSnippetTests {

	@RenderedSnippetTest
	void responseWithCookies(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseCookiesSnippet(Arrays.asList(cookieWithName("has_recent_activity").description("one"),
				cookieWithName("user_session").description("two")))
			.document(operationBuilder.response()
				.cookie("has_recent_activity", "true")
				.cookie("user_session", "1234abcd5678efgh")
				.build());
		assertThat(snippets.responseCookies()).isTable((table) -> table.withHeader("Name", "Description")
			.row("`has_recent_activity`", "one")
			.row("`user_session`", "two"));
	}

	@RenderedSnippetTest
	void ignoredResponseCookie(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseCookiesSnippet(Arrays.asList(cookieWithName("has_recent_activity").ignored(),
				cookieWithName("user_session").description("two")))
			.document(operationBuilder.response()
				.cookie("has_recent_activity", "true")
				.cookie("user_session", "1234abcd5678efgh")
				.build());
		assertThat(snippets.responseCookies())
			.isTable((table) -> table.withHeader("Name", "Description").row("`user_session`", "two"));
	}

	@RenderedSnippetTest
	void allUndocumentedResponseCookiesCanBeIgnored(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseCookiesSnippet(Arrays.asList(cookieWithName("has_recent_activity").description("one"),
				cookieWithName("user_session").description("two")), true)
			.document(operationBuilder.response()
				.cookie("has_recent_activity", "true")
				.cookie("user_session", "1234abcd5678efgh")
				.cookie("some_cookie", "value")
				.build());
		assertThat(snippets.responseCookies()).isTable((table) -> table.withHeader("Name", "Description")
			.row("`has_recent_activity`", "one")
			.row("`user_session`", "two"));
	}

	@RenderedSnippetTest
	void missingOptionalResponseCookie(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseCookiesSnippet(Arrays.asList(cookieWithName("has_recent_activity").description("one").optional(),
				cookieWithName("user_session").description("two")))
			.document(operationBuilder.response().cookie("user_session", "1234abcd5678efgh").build());
		assertThat(snippets.responseCookies()).isTable((table) -> table.withHeader("Name", "Description")
			.row("`has_recent_activity`", "one")
			.row("`user_session`", "two"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "response-cookies", template = "response-cookies-with-title")
	void responseCookiesWithCustomAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseCookiesSnippet(Collections.singletonList(cookieWithName("has_recent_activity").description("one")),
				attributes(key("title").value("Custom title")))
			.document(operationBuilder.response().cookie("has_recent_activity", "true").build());
		assertThat(snippets.responseCookies()).contains("Custom title");
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "response-cookies", template = "response-cookies-with-extra-column")
	void responseCookiesWithCustomDescriptorAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseCookiesSnippet(Arrays.asList(
				cookieWithName("has_recent_activity").description("one").attributes(key("foo").value("alpha")),
				cookieWithName("user_session").description("two").attributes(key("foo").value("bravo")),
				cookieWithName("color_theme").description("three").attributes(key("foo").value("charlie"))))
			.document(operationBuilder.response()
				.cookie("has_recent_activity", "true")
				.cookie("user_session", "1234abcd5678efgh")
				.cookie("color_theme", "high_contrast")
				.build());
		assertThat(snippets.responseCookies()).isTable((table) -> table.withHeader("Name", "Description", "Foo")
			.row("has_recent_activity", "one", "alpha")
			.row("user_session", "two", "bravo")
			.row("color_theme", "three", "charlie"));
	}

	@RenderedSnippetTest
	void additionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		CookieDocumentation
			.responseCookies(cookieWithName("has_recent_activity").description("one"),
					cookieWithName("user_session").description("two"))
			.and(cookieWithName("color_theme").description("three"))
			.document(operationBuilder.response()
				.cookie("has_recent_activity", "true")
				.cookie("user_session", "1234abcd5678efgh")
				.cookie("color_theme", "light")
				.build());
		assertThat(snippets.responseCookies()).isTable((table) -> table.withHeader("Name", "Description")
			.row("`has_recent_activity`", "one")
			.row("`user_session`", "two")
			.row("`color_theme`", "three"));
	}

	@RenderedSnippetTest
	void additionalDescriptorsWithRelaxedResponseCookies(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		CookieDocumentation.relaxedResponseCookies(cookieWithName("has_recent_activity").description("one"))
			.and(cookieWithName("color_theme").description("two"))
			.document(operationBuilder.response()
				.cookie("has_recent_activity", "true")
				.cookie("user_session", "1234abcd5678efgh")
				.cookie("color_theme", "light")
				.build());
		assertThat(snippets.responseCookies()).isTable((table) -> table.withHeader("Name", "Description")
			.row("`has_recent_activity`", "one")
			.row("`color_theme`", "two"));
	}

	@RenderedSnippetTest
	void tableCellContentIsEscapedWhenNecessary(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseCookiesSnippet(Collections.singletonList(cookieWithName("Foo|Bar").description("one|two")))
			.document(operationBuilder.response().cookie("Foo|Bar", "baz").build());
		assertThat(snippets.responseCookies())
			.isTable((table) -> table.withHeader("Name", "Description").row("`Foo|Bar`", "one|two"));
	}

}
