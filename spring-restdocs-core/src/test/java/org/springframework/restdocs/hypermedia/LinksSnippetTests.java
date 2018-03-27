/*
 * Copyright 2014-2018 the original author or authors.
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

package org.springframework.restdocs.hypermedia;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link LinksSnippet}.
 *
 * @author Andy Wilkinson
 */
public class LinksSnippetTests extends AbstractSnippetTests {

	public LinksSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void ignoredLink() throws IOException {
		this.snippets.expectLinks().withContents(
				tableWithHeader("Relation", "Description").row("`b`", "Link b"));
		new LinksSnippet(
				new StubLinkExtractor().withLinks(new Link("a", "alpha"),
						new Link("b", "bravo")),
				Arrays.asList(new LinkDescriptor("a").ignored(),
						new LinkDescriptor("b").description("Link b")))
								.document(this.operationBuilder.build());
	}

	@Test
	public void allUndocumentedLinksCanBeIgnored() throws IOException {
		this.snippets.expectLinks().withContents(
				tableWithHeader("Relation", "Description").row("`b`", "Link b"));
		new LinksSnippet(
				new StubLinkExtractor().withLinks(new Link("a", "alpha"),
						new Link("b", "bravo")),
				Arrays.asList(new LinkDescriptor("b").description("Link b")), true)
						.document(this.operationBuilder.build());
	}

	@Test
	public void presentOptionalLink() throws IOException {
		this.snippets.expectLinks().withContents(
				tableWithHeader("Relation", "Description").row("`foo`", "bar"));
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("foo", "blah")),
				Arrays.asList(new LinkDescriptor("foo").description("bar").optional()))
						.document(this.operationBuilder.build());
	}

	@Test
	public void missingOptionalLink() throws IOException {
		this.snippets.expectLinks().withContents(
				tableWithHeader("Relation", "Description").row("`foo`", "bar"));
		new LinksSnippet(new StubLinkExtractor(),
				Arrays.asList(new LinkDescriptor("foo").description("bar").optional()))
						.document(this.operationBuilder.build());
	}

	@Test
	public void documentedLinks() throws IOException {
		this.snippets.expectLinks()
				.withContents(tableWithHeader("Relation", "Description").row("`a`", "one")
						.row("`b`", "two"));
		new LinksSnippet(
				new StubLinkExtractor().withLinks(new Link("a", "alpha"),
						new Link("b", "bravo")),
				Arrays.asList(new LinkDescriptor("a").description("one"),
						new LinkDescriptor("b").description("two")))
								.document(this.operationBuilder.build());
	}

	@Test
	public void linkDescriptionFromTitleInPayload() throws IOException {
		this.snippets.expectLinks()
				.withContents(tableWithHeader("Relation", "Description").row("`a`", "one")
						.row("`b`", "Link b"));
		new LinksSnippet(
				new StubLinkExtractor().withLinks(new Link("a", "alpha", "Link a"),
						new Link("b", "bravo", "Link b")),
				Arrays.asList(new LinkDescriptor("a").description("one"),
						new LinkDescriptor("b"))).document(this.operationBuilder.build());
	}

	@Test
	public void linksWithCustomAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("links"))
				.willReturn(snippetResource("links-with-title"));
		this.snippets.expectLinks().withContents(containsString("Title for the links"));

		new LinksSnippet(
				new StubLinkExtractor().withLinks(new Link("a", "alpha"),
						new Link("b", "bravo")),
				Arrays.asList(new LinkDescriptor("a").description("one"),
						new LinkDescriptor("b").description("two")),
				attributes(key("title").value("Title for the links")))
						.document(
								this.operationBuilder
										.attribute(TemplateEngine.class.getName(),
												new MustacheTemplateEngine(resolver))
										.build());
	}

	@Test
	public void linksWithCustomDescriptorAttributes() throws IOException {
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("links"))
				.willReturn(snippetResource("links-with-extra-column"));
		this.snippets.expectLinks()
				.withContents(tableWithHeader("Relation", "Description", "Foo")
						.row("a", "one", "alpha").row("b", "two", "bravo"));

		new LinksSnippet(
				new StubLinkExtractor().withLinks(new Link("a", "alpha"),
						new Link("b", "bravo")),
				Arrays.asList(
						new LinkDescriptor("a").description("one")
								.attributes(key("foo").value("alpha")),
						new LinkDescriptor("b").description("two")
								.attributes(key("foo").value("bravo"))))
										.document(this.operationBuilder.attribute(
												TemplateEngine.class.getName(),
												new MustacheTemplateEngine(resolver))
												.build());
	}

	@Test
	public void additionalDescriptors() throws IOException {
		this.snippets.expectLinks()
				.withContents(tableWithHeader("Relation", "Description").row("`a`", "one")
						.row("`b`", "two"));
		HypermediaDocumentation
				.links(new StubLinkExtractor().withLinks(new Link("a", "alpha"),
						new Link("b", "bravo")),
						new LinkDescriptor("a").description("one"))
				.and(new LinkDescriptor("b").description("two"))
				.document(this.operationBuilder.build());
	}

	@Test
	public void tableCellContentIsEscapedWhenNecessary() throws IOException {
		this.snippets.expectLinks()
				.withContents(tableWithHeader("Relation", "Description").row(
						escapeIfNecessary("`Foo|Bar`"), escapeIfNecessary("one|two")));
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("Foo|Bar", "foo")),
				Arrays.asList(new LinkDescriptor("Foo|Bar").description("one|two")))
						.document(this.operationBuilder.build());
	}

	private String escapeIfNecessary(String input) {
		if (this.templateFormat.equals(TemplateFormats.markdown())) {
			return input;
		}
		return input.replace("|", "\\|");
	}

}
