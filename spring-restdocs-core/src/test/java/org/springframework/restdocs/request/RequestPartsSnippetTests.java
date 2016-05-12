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

package org.springframework.restdocs.request;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link RequestPartsSnippet}.
 *
 * @author Andy Wilkinson
 */
public class RequestPartsSnippetTests extends AbstractSnippetTests {

	public RequestPartsSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void requestParts() throws IOException {
		this.snippet.expectRequestParts("request-parts")
				.withContents(tableWithHeader("Part", "Description").row("`a`", "one")
						.row("`b`", "two"));
		new RequestPartsSnippet(Arrays.asList(partWithName("a").description("one"),
				partWithName("b").description("two")))
						.document(operationBuilder("request-parts")
								.request("http://localhost").part("a", "bravo".getBytes())
								.and().part("b", "bravo".getBytes()).build());
	}

	@Test
	public void ignoredRequestPart() throws IOException {
		this.snippet.expectRequestParts("ignored-request-part")
				.withContents(tableWithHeader("Part", "Description").row("`b`", "two"));
		new RequestPartsSnippet(Arrays.asList(partWithName("a").ignored(),
				partWithName("b").description("two")))
						.document(operationBuilder("ignored-request-part")
								.request("http://localhost").part("a", "bravo".getBytes())
								.and().part("b", "bravo".getBytes()).build());
	}

	@Test
	public void allUndocumentedRequestPartsCanBeIgnored() throws IOException {
		this.snippet.expectRequestParts("ignore-all-undocumented")
				.withContents(tableWithHeader("Part", "Description").row("`b`", "two"));
		new RequestPartsSnippet(Arrays.asList(partWithName("b").description("two")), true)
				.document(operationBuilder("ignore-all-undocumented")
						.request("http://localhost").part("a", "bravo".getBytes()).and()
						.part("b", "bravo".getBytes()).build());
	}

	@Test
	public void missingOptionalRequestPart() throws IOException {
		this.snippet.expectRequestParts("missing-optional-request-parts")
				.withContents(tableWithHeader("Part", "Description").row("`a`", "one")
						.row("`b`", "two"));
		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one").optional(),
						partWithName("b").description("two"))).document(
								operationBuilder("missing-optional-request-parts")
										.request("http://localhost")
										.part("b", "bravo".getBytes()).build());
	}

	@Test
	public void presentOptionalRequestPart() throws IOException {
		this.snippet.expectRequestParts("present-optional-request-part")
				.withContents(tableWithHeader("Part", "Description").row("`a`", "one"));
		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one").optional()))
						.document(operationBuilder("present-optional-request-part")
								.request("http://localhost").part("a", "one".getBytes())
								.build());
	}

	@Test
	public void requestPartsWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-parts"))
				.willReturn(snippetResource("request-parts-with-title"));
		this.snippet.expectRequestParts("request-parts-with-custom-attributes")
				.withContents(containsString("The title"));

		new RequestPartsSnippet(
				Arrays.asList(
						partWithName("a").description("one")
								.attributes(key("foo").value("alpha")),
						partWithName("b").description("two")
								.attributes(key("foo").value("bravo"))),
				attributes(key("title").value("The title")))
						.document(operationBuilder("request-parts-with-custom-attributes")
								.attribute(TemplateEngine.class.getName(),
										new MustacheTemplateEngine(resolver))
								.request("http://localhost").part("a", "alpha".getBytes())
								.and().part("b", "bravo".getBytes()).build());
	}

	@Test
	public void requestPartsWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-parts"))
				.willReturn(snippetResource("request-parts-with-extra-column"));
		this.snippet.expectRequestParts("request-parts-with-custom-descriptor-attributes")
				.withContents(tableWithHeader("Part", "Description", "Foo")
						.row("a", "one", "alpha").row("b", "two", "bravo"));

		new RequestPartsSnippet(Arrays.asList(
				partWithName("a").description("one")
						.attributes(key("foo").value("alpha")),
				partWithName("b").description("two")
						.attributes(key("foo").value("bravo"))))
								.document(operationBuilder(
										"request-parts-with-custom-descriptor-attributes")
												.attribute(TemplateEngine.class.getName(),
														new MustacheTemplateEngine(
																resolver))
												.request("http://localhost")
												.part("a", "alpha".getBytes()).and()
												.part("b", "bravo".getBytes()).build());
	}

	@Test
	public void requestPartsWithOptionalColumn() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-parts"))
				.willReturn(snippetResource("request-parts-with-optional-column"));
		this.snippet.expectRequestParts("request-parts-with-optional-column")
				.withContents(tableWithHeader("Part", "Optional", "Description")
						.row("a", "true", "one").row("b", "false", "two"));

		new RequestPartsSnippet(
				Arrays.asList(partWithName("a").description("one").optional(),
						partWithName("b").description("two"))).document(
								operationBuilder("request-parts-with-optional-column")
										.attribute(TemplateEngine.class.getName(),
												new MustacheTemplateEngine(resolver))
										.request("http://localhost")
										.part("a", "alpha".getBytes()).and()
										.part("b", "bravo".getBytes()).build());
	}

	@Test
	public void additionalDescriptors() throws IOException {
		this.snippet.expectRequestParts("additional-descriptors")
				.withContents(tableWithHeader("Part", "Description").row("`a`", "one")
						.row("`b`", "two"));
		RequestDocumentation.requestParts(partWithName("a").description("one"))
				.and(partWithName("b").description("two"))
				.document(operationBuilder("additional-descriptors")
						.request("http://localhost").part("a", "bravo".getBytes()).and()
						.part("b", "bravo".getBytes()).build());
	}

}
