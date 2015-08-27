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

package org.springframework.restdocs.hypermedia;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;
import org.springframework.restdocs.test.OperationBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Tests for {@link LinksSnippet}
 * 
 * @author Andy Wilkinson
 */
public class LinksSnippetTests {

	@Rule
	public ExpectedSnippet snippet = new ExpectedSnippet();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void undocumentedLink() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Links with the following relations were not"
				+ " documented: [foo]"));
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("foo", "bar")),
				Collections.<LinkDescriptor> emptyList()).document(new OperationBuilder(
				"undocumented-link").build());
	}

	@Test
	public void missingLink() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Links with the following relations were not"
				+ " found in the response: [foo]"));
		new LinksSnippet(new StubLinkExtractor(), Arrays.asList(new LinkDescriptor("foo")
				.description("bar"))).document(new OperationBuilder("missing-link")
				.build());
	}

	@Test
	public void documentedOptionalLink() throws IOException {
		this.snippet.expectLinks("documented-optional-link").withContents( //
				tableWithHeader("Relation", "Description") //
						.row("foo", "bar"));
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("foo", "blah")),
				Arrays.asList(new LinkDescriptor("foo").description("bar").optional()))
				.document(new OperationBuilder("documented-optional-link").build());
	}

	@Test
	public void missingOptionalLink() throws IOException {
		this.snippet.expectLinks("missing-optional-link").withContents( //
				tableWithHeader("Relation", "Description") //
						.row("foo", "bar"));
		new LinksSnippet(new StubLinkExtractor(), Arrays.asList(new LinkDescriptor("foo")
				.description("bar").optional())).document(new OperationBuilder(
				"missing-optional-link").build());
	}

	@Test
	public void undocumentedLinkAndMissingLink() throws IOException {
		this.thrown.expect(SnippetException.class);
		this.thrown.expectMessage(equalTo("Links with the following relations were not"
				+ " documented: [a]. Links with the following relations were not"
				+ " found in the response: [foo]"));
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("a", "alpha")),
				Arrays.asList(new LinkDescriptor("foo").description("bar")))
				.document(new OperationBuilder("undocumented-link-and-missing-link")
						.build());
	}

	@Test
	public void documentedLinks() throws IOException {
		this.snippet.expectLinks("documented-links").withContents( //
				tableWithHeader("Relation", "Description") //
						.row("a", "one") //
						.row("b", "two"));
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("a", "alpha"),
				new Link("b", "bravo")), Arrays.asList(
				new LinkDescriptor("a").description("one"),
				new LinkDescriptor("b").description("two")))
				.document(new OperationBuilder("documented-links").build());
	}

	@Test
	public void linksWithCustomDescriptorAttributes() throws IOException {
		this.snippet.expectLinks("links-with-custom-descriptor-attributes").withContents( //
				tableWithHeader("Relation", "Description", "Foo") //
						.row("a", "one", "alpha") //
						.row("b", "two", "bravo"));
		MockHttpServletRequest request = new MockHttpServletRequest();
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("links"))
				.thenReturn(
						new FileSystemResource(
								"src/test/resources/custom-snippet-templates/links-with-extra-column.snippet"));
		request.setAttribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(
				resolver));
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("a", "alpha"),
				new Link("b", "bravo")), Arrays.asList(
				new LinkDescriptor("a").description("one").attributes(
						key("foo").value("alpha")),
				new LinkDescriptor("b").description("two").attributes(
						key("foo").value("bravo")))).document(new OperationBuilder(
				"links-with-custom-descriptor-attributes").attribute(
				TemplateEngine.class.getName(), new MustacheTemplateEngine(resolver))
				.build());
	}

	@Test
	public void linksWithCustomAttributes() throws IOException {
		this.snippet.expectLinks("links-with-custom-attributes").withContents(
				startsWith(".Title for the links"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("links"))
				.thenReturn(
						new FileSystemResource(
								"src/test/resources/custom-snippet-templates/links-with-title.snippet"));
		new LinksSnippet(new StubLinkExtractor().withLinks(new Link("a", "alpha"),
				new Link("b", "bravo")), attributes(key("title").value(
				"Title for the links")), Arrays.asList(
				new LinkDescriptor("a").description("one"),
				new LinkDescriptor("b").description("two")))
				.document(new OperationBuilder("links-with-custom-attributes").attribute(
						TemplateEngine.class.getName(),
						new MustacheTemplateEngine(resolver)).build());
	}

	private static class StubLinkExtractor implements LinkExtractor {

		private MultiValueMap<String, Link> linksByRel = new LinkedMultiValueMap<String, Link>();

		@Override
		public MultiValueMap<String, Link> extractLinks(OperationResponse response)
				throws IOException {
			return this.linksByRel;
		}

		private StubLinkExtractor withLinks(Link... links) {
			for (Link link : links) {
				this.linksByRel.add(link.getRel(), link);
			}
			return this;
		}

	}

}
