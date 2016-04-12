/*
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.OperationBuilder;

import static org.hamcrest.CoreMatchers.containsString;
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
		this.snippet.expectRequestHeaders("request-with-headers")
				.withContents(tableWithHeader("Name", "Description").row("X-Test", "one")
						.row("Accept", "two").row("Accept-Encoding", "three")
						.row("Accept-Language", "four").row("Cache-Control", "five")
						.row("Connection", "six"));
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one"),
						headerWithName("Accept").description("two"),
						headerWithName("Accept-Encoding").description("three"),
						headerWithName("Accept-Language").description("four"),
						headerWithName("Cache-Control").description("five"),
						headerWithName("Connection").description("six")))
								.document(
										operationBuilder("request-with-headers")
												.request("http://localhost")
												.header("X-Test", "test")
												.header("Accept", "*/*")
												.header("Accept-Encoding",
														"gzip, deflate")
								.header("Accept-Language", "en-US,en;q=0.5")
								.header("Cache-Control", "max-age=0")
								.header("Connection", "keep-alive").build());
	}

	@Test
	public void caseInsensitiveRequestHeaders() throws IOException {
		this.snippet.expectRequestHeaders("case-insensitive-request-headers")
				.withContents(
						tableWithHeader("Name", "Description").row("X-Test", "one"));
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one")))
						.document(operationBuilder("case-insensitive-request-headers")
								.request("/").header("X-test", "test").build());
	}

	@Test
	public void undocumentedRequestHeader() throws IOException {
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one")))
						.document(new OperationBuilder("undocumented-request-header",
								this.snippet.getOutputDirectory())
										.request("http://localhost")
										.header("X-Test", "test").header("Accept", "*/*")
										.build());
	}

	@Test
	public void requestHeadersWithCustomAttributes() throws IOException {
		this.snippet.expectRequestHeaders("request-headers-with-custom-attributes")
				.withContents(containsString("Custom title"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-headers"))
				.willReturn(snippetResource("request-headers-with-title"));
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one")),
				attributes(key("title").value("Custom title"))).document(
						operationBuilder("request-headers-with-custom-attributes")
								.attribute(TemplateEngine.class.getName(),
										new MustacheTemplateEngine(resolver))
								.request("http://localhost").header("X-Test", "test")
								.build());
	}

	@Test
	public void requestHeadersWithCustomDescriptorAttributes() throws IOException {
		this.snippet
				.expectRequestHeaders("request-headers-with-custom-descriptor-attributes")
				.withContents(//
						tableWithHeader("Name", "Description", "Foo")
								.row("X-Test", "one", "alpha")
								.row("Accept-Encoding", "two", "bravo")
								.row("Accept", "three", "charlie"));
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
								.document(operationBuilder(
										"request-headers-with-custom-descriptor-attributes")
												.attribute(TemplateEngine.class.getName(),
														new MustacheTemplateEngine(
																resolver))
												.request("http://localhost")
												.header("X-Test", "test")
												.header("Accept-Encoding",
														"gzip, deflate")
										.header("Accept", "*/*").build());
	}

	@Test
	public void additionalDescriptors() throws IOException {
		this.snippet.expectRequestHeaders("additional-descriptors")
				.withContents(tableWithHeader("Name", "Description").row("X-Test", "one")
						.row("Accept", "two").row("Accept-Encoding", "three")
						.row("Accept-Language", "four").row("Cache-Control", "five")
						.row("Connection", "six"));
		HeaderDocumentation
				.requestHeaders(headerWithName("X-Test").description("one"),
						headerWithName("Accept").description("two"),
						headerWithName("Accept-Encoding").description("three"),
						headerWithName("Accept-Language").description("four"))
				.and(headerWithName("Cache-Control").description("five"),
						headerWithName("Connection").description("six"))
				.document(operationBuilder("additional-descriptors")
						.request("http://localhost").header("X-Test", "test")
						.header("Accept", "*/*")
						.header("Accept-Encoding", "gzip, deflate")
						.header("Accept-Language", "en-US,en;q=0.5")
						.header("Cache-Control", "max-age=0")
						.header("Connection", "keep-alive").build());
	}

}
