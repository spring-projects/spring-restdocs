/*
 * Copyright 2014-present the original author or authors.
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

package org.springframework.restdocs.hypermedia;

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
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link LinksSnippet}.
 *
 * @author Andy Wilkinson
 */
class LinksSnippetTests {

	@RenderedSnippetTest
	void ignoredLink(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("a", "alpha"), new Link("b", "bravo")),
				Arrays.asList(new LinkDescriptor("a").ignored(), new LinkDescriptor("b").description("Link b")))
			.document(operationBuilder.build());
		assertThat(snippets.links())
			.isTable((table) -> table.withHeader("Relation", "Description").row("`b`", "Link b"));
	}

	@RenderedSnippetTest
	void allUndocumentedLinksCanBeIgnored(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("a", "alpha"), new Link("b", "bravo")),
				Arrays.asList(new LinkDescriptor("b").description("Link b")), true)
			.document(operationBuilder.build());
		assertThat(snippets.links())
			.isTable((table) -> table.withHeader("Relation", "Description").row("`b`", "Link b"));
	}

	@RenderedSnippetTest
	void presentOptionalLink(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("foo", "blah")),
				Arrays.asList(new LinkDescriptor("foo").description("bar").optional()))
			.document(operationBuilder.build());
		assertThat(snippets.links())
			.isTable((table) -> table.withHeader("Relation", "Description").row("`foo`", "bar"));
	}

	@RenderedSnippetTest
	void missingOptionalLink(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new LinksSnippet(new StubLinkExtractor(),
				Arrays.asList(new LinkDescriptor("foo").description("bar").optional()))
			.document(operationBuilder.build());
		assertThat(snippets.links())
			.isTable((table) -> table.withHeader("Relation", "Description").row("`foo`", "bar"));
	}

	@RenderedSnippetTest
	void documentedLinks(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("a", "alpha"), new Link("b", "bravo")),
				Arrays.asList(new LinkDescriptor("a").description("one"), new LinkDescriptor("b").description("two")))
			.document(operationBuilder.build());
		assertThat(snippets.links())
			.isTable((table) -> table.withHeader("Relation", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void linkDescriptionFromTitleInPayload(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new LinksSnippet(
				new StubLinkExtractor().withLinks(new Link("a", "alpha", "Link a"), new Link("b", "bravo", "Link b")),
				Arrays.asList(new LinkDescriptor("a").description("one"), new LinkDescriptor("b")))
			.document(operationBuilder.build());
		assertThat(snippets.links())
			.isTable((table) -> table.withHeader("Relation", "Description").row("`a`", "one").row("`b`", "Link b"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "links", template = "links-with-title")
	void linksWithCustomAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("a", "alpha"), new Link("b", "bravo")),
				Arrays.asList(new LinkDescriptor("a").description("one"), new LinkDescriptor("b").description("two")),
				attributes(key("title").value("Title for the links")))
			.document(operationBuilder.build());
		assertThat(snippets.links()).contains("Title for the links");
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "links", template = "links-with-extra-column")
	void linksWithCustomDescriptorAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("a", "alpha"), new Link("b", "bravo")),
				Arrays.asList(new LinkDescriptor("a").description("one").attributes(key("foo").value("alpha")),
						new LinkDescriptor("b").description("two").attributes(key("foo").value("bravo"))))
			.document(operationBuilder.build());
		assertThat(snippets.links()).isTable((table) -> table.withHeader("Relation", "Description", "Foo")
			.row("a", "one", "alpha")
			.row("b", "two", "bravo"));
	}

	@RenderedSnippetTest
	void additionalDescriptors(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		HypermediaDocumentation
			.links(new StubLinkExtractor().withLinks(new Link("a", "alpha"), new Link("b", "bravo")),
					new LinkDescriptor("a").description("one"))
			.and(new LinkDescriptor("b").description("two"))
			.document(operationBuilder.build());
		assertThat(snippets.links())
			.isTable((table) -> table.withHeader("Relation", "Description").row("`a`", "one").row("`b`", "two"));
	}

	@RenderedSnippetTest
	void tableCellContentIsEscapedWhenNecessary(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("Foo|Bar", "foo")),
				Arrays.asList(new LinkDescriptor("Foo|Bar").description("one|two")))
			.document(operationBuilder.build());
		assertThat(snippets.links())
			.isTable((table) -> table.withHeader("Relation", "Description").row("`Foo|Bar`", "one|two"));
	}

	@SnippetTest
	void undocumentedLink(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new LinksSnippet(new StubLinkExtractor().withLinks(new Link("foo", "bar")),
					Collections.<LinkDescriptor>emptyList())
				.document(operationBuilder.build()))
			.withMessage("Links with the following relations were not documented: [foo]");
	}

	@SnippetTest
	void missingLink(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new LinksSnippet(new StubLinkExtractor(),
					Arrays.asList(new LinkDescriptor("foo").description("bar")))
				.document(operationBuilder.build()))
			.withMessage("Links with the following relations were not found in the response: [foo]");
	}

	@SnippetTest
	void undocumentedLinkAndMissingLink(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new LinksSnippet(new StubLinkExtractor().withLinks(new Link("a", "alpha")),
					Arrays.asList(new LinkDescriptor("foo").description("bar")))
				.document(operationBuilder.build()))
			.withMessage("Links with the following relations were not documented: [a]. Links with the following"
					+ " relations were not found in the response: [foo]");
	}

	@SnippetTest
	void linkWithNoDescription(OperationBuilder operationBuilder) {
		assertThatExceptionOfType(SnippetException.class)
			.isThrownBy(() -> new LinksSnippet(new StubLinkExtractor().withLinks(new Link("foo", "bar")),
					Arrays.asList(new LinkDescriptor("foo")))
				.document(operationBuilder.build()))
			.withMessage("No description was provided for the link with rel 'foo' and no title was available"
					+ " from the link in the payload");
	}

}
