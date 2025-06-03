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

import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTemplate;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link RequestCookiesSnippet}.
 *
 * @author Clyde Stubbs
 * @author Andy Wilkinson
 */
class RequestCookiesSnippetTests {

	@RenderedSnippetTest
	void requestWithCookies(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestCookiesSnippet(
				Arrays.asList(cookieWithName("tz").description("one"), cookieWithName("logged_in").description("two")))
			.document(operationBuilder.request("http://localhost")
				.cookie("tz", "Europe%2FLondon")
				.cookie("logged_in", "true")
				.build());
		assertThat(snippets.requestCookies())
			.isTable((table) -> table.withHeader("Name", "Description").row("`tz`", "one").row("`logged_in`", "two"));
	}

	@RenderedSnippetTest
	void ignoredRequestCookie(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestCookiesSnippet(
				Arrays.asList(cookieWithName("tz").ignored(), cookieWithName("logged_in").description("two")))
			.document(operationBuilder.request("http://localhost")
				.cookie("tz", "Europe%2FLondon")
				.cookie("logged_in", "true")
				.build());
		assertThat(snippets.requestCookies())
			.isTable((table) -> table.withHeader("Name", "Description").row("`logged_in`", "two"));
	}

	@RenderedSnippetTest
	void allUndocumentedCookiesCanBeIgnored(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestCookiesSnippet(
				Arrays.asList(cookieWithName("tz").description("one"), cookieWithName("logged_in").description("two")),
				true)
			.document(operationBuilder.request("http://localhost")
				.cookie("tz", "Europe%2FLondon")
				.cookie("logged_in", "true")
				.cookie("user_session", "abcd1234efgh5678")
				.build());
		assertThat(snippets.requestCookies())
			.isTable((table) -> table.withHeader("Name", "Description").row("`tz`", "one").row("`logged_in`", "two"));
	}

	@RenderedSnippetTest
	void missingOptionalCookie(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestCookiesSnippet(Arrays.asList(cookieWithName("tz").description("one").optional(),
				cookieWithName("logged_in").description("two")))
			.document(operationBuilder.request("http://localhost").cookie("logged_in", "true").build());
		assertThat(snippets.requestCookies())
			.isTable((table) -> table.withHeader("Name", "Description").row("`tz`", "one").row("`logged_in`", "two"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "request-cookies", template = "request-cookies-with-title")
	void requestCookiesWithCustomAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestCookiesSnippet(Collections.singletonList(cookieWithName("tz").description("one")),
				attributes(key("title").value("Custom title")))
			.document(operationBuilder.request("http://localhost").cookie("tz", "Europe%2FLondon").build());
		assertThat(snippets.requestCookies()).contains("Custom title");
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "request-cookies", template = "request-cookies-with-extra-column")
	void requestCookiesWithCustomDescriptorAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestCookiesSnippet(
				Arrays.asList(cookieWithName("tz").description("one").attributes(key("foo").value("alpha")),
						cookieWithName("logged_in").description("two").attributes(key("foo").value("bravo"))))
			.document(operationBuilder.request("http://localhost")
				.cookie("tz", "Europe%2FLondon")
				.cookie("logged_in", "true")
				.build());
		assertThat(snippets.requestCookies()).isTable((table) -> table.withHeader("Name", "Description", "Foo")
			.row("tz", "one", "alpha")
			.row("logged_in", "two", "bravo"));
	}

	@RenderedSnippetTest
	void additionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new RequestCookiesSnippet(
				Arrays.asList(cookieWithName("tz").description("one"), cookieWithName("logged_in").description("two")))
			.and(cookieWithName("user_session").description("three"))
			.document(operationBuilder.request("http://localhost")
				.cookie("tz", "Europe%2FLondon")
				.cookie("logged_in", "true")
				.cookie("user_session", "abcd1234efgh5678")
				.build());
		assertThat(snippets.requestCookies()).isTable((table) -> table.withHeader("Name", "Description")
			.row("`tz`", "one")
			.row("`logged_in`", "two")
			.row("`user_session`", "three"));
	}

	@RenderedSnippetTest
	void additionalDescriptorsWithRelaxedRequestCookies(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestCookiesSnippet(
				Arrays.asList(cookieWithName("tz").description("one"), cookieWithName("logged_in").description("two")),
				true)
			.and(cookieWithName("user_session").description("three"))
			.document(operationBuilder.request("http://localhost")
				.cookie("tz", "Europe%2FLondon")
				.cookie("logged_in", "true")
				.cookie("user_session", "abcd1234efgh5678")
				.cookie("color_theme", "light")
				.build());
		assertThat(snippets.requestCookies()).isTable((table) -> table.withHeader("Name", "Description")
			.row("`tz`", "one")
			.row("`logged_in`", "two")
			.row("`user_session`", "three"));
	}

	@RenderedSnippetTest
	void tableCellContentIsEscapedWhenNecessary(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new RequestCookiesSnippet(Collections.singletonList(cookieWithName("Foo|Bar").description("one|two")))
			.document(operationBuilder.request("http://localhost").cookie("Foo|Bar", "baz").build());
		assertThat(snippets.requestCookies())
			.isTable((table) -> table.withHeader("Name", "Description").row("`Foo|Bar`", "one|two"));
	}

	@SnippetTest
	void missingRequestCookie(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestCookiesSnippet(
					Collections.singletonList(CookieDocumentation.cookieWithName("JSESSIONID").description("one")))
				.document(operationBuilder.request("http://localhost").build()))
			.withMessage("Cookies with the following names were not found in the request: [JSESSIONID]");
	}

	@SnippetTest
	void undocumentedRequestCookie(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new RequestCookiesSnippet(Collections.emptyList()).document(
					operationBuilder.request("http://localhost").cookie("JSESSIONID", "1234abcd5678efgh").build()))
			.withMessageEndingWith("Cookies with the following names were not documented: [JSESSIONID]");
	}

}
