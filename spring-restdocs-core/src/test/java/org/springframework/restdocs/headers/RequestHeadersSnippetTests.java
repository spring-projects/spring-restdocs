/*
 * Copyright 2014-2015 the original author or authors.
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;
import org.springframework.restdocs.test.OperationBuilder;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;

/**
 * Tests for {@link RequestHeadersSnippet}.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 */
public class RequestHeadersSnippetTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Rule
	public final ExpectedSnippet snippet = new ExpectedSnippet();

	@Test
	public void requestWithHeaders() throws IOException {
		this.snippet.expectRequestHeaders("request-with-headers")
				.withContents(tableWithHeader("Name", "Description").row("X-Test", "one")
						.row("Accept", "two").row("Accept-Encoding", "three")
						.row("Accept-Language", "four").row("Cache-Control", "five")
						.row("Connection", "six"));
		new RequestHeadersSnippet(Arrays.asList(
				headerWithName("X-Test").description("one"),
				headerWithName("Accept").description("two"),
				headerWithName("Accept-Encoding").description("three"),
				headerWithName("Accept-Language").description("four"),
				headerWithName("Cache-Control").description("five"),
				headerWithName("Connection").description("six")))
						.document(new OperationBuilder("request-with-headers",
								this.snippet.getOutputDirectory())
										.request("http://localhost")
										.header("X-Test", "test").header("Accept", "*/*")
										.header("Accept-Encoding", "gzip, deflate")
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
						.document(new OperationBuilder("case-insensitive-request-headers",
								this.snippet.getOutputDirectory()).request("/")
										.header("X-test", "test").build());
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
	public void missingRequestHeader() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Headers with the following names were not found"
						+ " in the request: [Accept]"));
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("Accept").description("one")))
						.document(new OperationBuilder("missing-request-headers",
								this.snippet.getOutputDirectory())
										.request("http://localhost").build());
	}

	@Test
	public void undocumentedRequestHeaderAndMissingRequestHeader() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(endsWith("Headers with the following names were not found"
						+ " in the request: [Accept]"));
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("Accept").description("one")))
						.document(new OperationBuilder(
								"undocumented-request-header-and-missing-request-header",
								this.snippet.getOutputDirectory())
										.request("http://localhost")
										.header("X-Test", "test").build());
	}

	@Test
	public void requestHeadersWithCustomDescriptorAttributes() throws IOException {
		this.snippet.expectRequestHeaders("request-headers-with-custom-attributes")
				.withContents(//
						tableWithHeader("Name", "Description", "Foo")
								.row("X-Test", "one", "alpha")
								.row("Accept-Encoding", "two", "bravo")
								.row("Accept", "three", "charlie"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-headers"))
				.willReturn(snippetResource("request-headers-with-extra-column"));
		new RequestHeadersSnippet(
				Arrays.asList(
						headerWithName("X-Test").description("one")
								.attributes(key("foo").value(
										"alpha")),
						headerWithName("Accept-Encoding").description("two")
								.attributes(key("foo").value("bravo")),
						headerWithName("Accept").description("three").attributes(key(
								"foo").value("charlie")))).document(new OperationBuilder(
										"request-headers-with-custom-attributes",
										this.snippet.getOutputDirectory())
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
	public void requestHeadersWithCustomAttributes() throws IOException {
		this.snippet.expectRequestHeaders("request-headers-with-custom-attributes")
				.withContents(startsWith(".Custom title"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-headers"))
				.willReturn(snippetResource("request-headers-with-title"));
		new RequestHeadersSnippet(
				Arrays.asList(headerWithName("X-Test").description("one")),
				attributes(key("title").value("Custom title"))).document(
						new OperationBuilder("request-headers-with-custom-attributes",
								this.snippet.getOutputDirectory())
										.attribute(TemplateEngine.class.getName(),
												new MustacheTemplateEngine(resolver))
										.request("http://localhost")
										.header("X-Test", "test").build());
	}

	private FileSystemResource snippetResource(String name) {
		return new FileSystemResource(
				"src/test/resources/custom-snippet-templates/" + name + ".snippet");
	}

}
