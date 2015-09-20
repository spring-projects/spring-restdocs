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
 * Tests for {@link ReponseHeadersSnippet}.
 *
 * @author Andreas Evers
 */
public class ResponseHeadersSnippetTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Rule
	public final ExpectedSnippet snippet = new ExpectedSnippet();

	@Test
	public void responseWithHeaders() throws IOException {
		this.snippet.expectResponseHeaders("response-headers").withContents(//
				tableWithHeader("Name", "Description") //
						.row("X-Test", "one") //
						.row("Content-Type", "two") //
						.row("Etag", "three") //
						.row("Content-Length", "four") //
						.row("Cache-Control", "five") //
						.row("Vary", "six"));
		new ResponseHeadersSnippet(Arrays.asList(
				headerWithName("X-Test").description("one"), //
				headerWithName("Content-Type").description("two"), //
				headerWithName("Etag").description("three"), //
				headerWithName("Content-Length").description("four"), //
				headerWithName("Cache-Control").description("five"), //
				headerWithName("Vary").description("six"))) //
				.document(new OperationBuilder("response-headers", this.snippet
						.getOutputDirectory()) //
						.response() //
						.header("X-Test", "test") //
						.header("Content-Type", "application/json") //
						.header("Etag", "lskjadldj3ii32l2ij23") //
						.header("Content-Length", "19166") //
						.header("Cache-Control", "max-age=0") //
						.header("Vary", "User-Agent") //
						.build());
	}

	@Test
	public void responseHeadersWithCustomDescriptorAttributes() throws IOException {
		this.snippet.expectResponseHeaders("response-headers-with-custom-attributes")
				.withContents(//
						tableWithHeader("Name", "Description", "Foo") //
								.row("X-Test", "one", "alpha") //
								.row("Content-Type", "two", "bravo") //
								.row("Etag", "three", "charlie"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-headers")).willReturn(
				snippetResource("response-headers-with-extra-column"));
		new ResponseHeadersSnippet(Arrays.asList(
				headerWithName("X-Test").description("one").attributes(
						key("foo").value("alpha")),
				headerWithName("Content-Type").description("two").attributes(
						key("foo").value("bravo")),
				headerWithName("Etag").description("three").attributes(
						key("foo").value("charlie")))).document(new OperationBuilder(
				"response-headers-with-custom-attributes", this.snippet
						.getOutputDirectory())
				.attribute(TemplateEngine.class.getName(),
						new MustacheTemplateEngine(resolver)).response()
				.header("X-Test", "test").header("Content-Type", "application/json")
				.header("Etag", "lskjadldj3ii32l2ij23").build());
	}

	@Test
	public void responseHeadersWithCustomAttributes() throws IOException {
		this.snippet.expectResponseHeaders("response-headers-with-custom-attributes")
				.withContents(startsWith(".Custom title"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("response-headers")).willReturn(
				snippetResource("response-headers-with-title"));
		new ResponseHeadersSnippet(Arrays.asList(headerWithName("X-Test").description(
				"one")), attributes(key("title").value("Custom title")))
				.document(new OperationBuilder("response-headers-with-custom-attributes",
						this.snippet.getOutputDirectory())
						.attribute(TemplateEngine.class.getName(),
								new MustacheTemplateEngine(resolver)).response()
						.header("X-Test", "test").build());
	}

	@Test
	public void undocumentedResponseHeader() throws IOException {
		new ResponseHeadersSnippet(Arrays.asList(headerWithName("X-Test").description(
				"one"))).document(new OperationBuilder("undocumented-response-header",
				this.snippet.getOutputDirectory()).response().header("X-Test", "test")
				.header("Content-Type", "*/*").build());
	}

	@Test
	public void missingResponseHeader() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(equalTo("Headers with the following names were not found"
						+ " in the response: [Content-Type]"));
		new ResponseHeadersSnippet(Arrays.asList(headerWithName("Content-Type")
				.description("one"))).document(new OperationBuilder(
				"missing-response-headers", this.snippet.getOutputDirectory()).response()
				.build());
	}

	@Test
	public void undocumentedResponseHeaderAndMissingResponseHeader() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown
				.expectMessage(endsWith("Headers with the following names were not found"
						+ " in the response: [Content-Type]"));
		new ResponseHeadersSnippet(Arrays.asList(headerWithName("Content-Type")
				.description("one"))).document(new OperationBuilder(
				"undocumented-response-header-and-missing-response-header", this.snippet
						.getOutputDirectory()).response().header("X-Test", "test")
				.build());
	}

	private FileSystemResource snippetResource(String name) {
		return new FileSystemResource("src/test/resources/custom-snippet-templates/"
				+ name + ".snippet");
	}

}
