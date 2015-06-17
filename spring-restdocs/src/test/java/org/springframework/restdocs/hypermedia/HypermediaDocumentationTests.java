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
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.documentLinks;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;
import static org.springframework.restdocs.test.StubMvcResult.result;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.snippet.SnippetGenerationException;
import org.springframework.restdocs.test.ExpectedSnippet;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Tests for {@link HypermediaDocumentation}
 * 
 * @author Andy Wilkinson
 */
public class HypermediaDocumentationTests {

	@Rule
	public ExpectedSnippet snippet = new ExpectedSnippet();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void undocumentedLink() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown.expectMessage(equalTo("Links with the following relations were not"
				+ " documented: [foo]"));
		documentLinks("undocumented-link",
				new StubLinkExtractor().withLinks(new Link("foo", "bar"))).handle(
				result());
	}

	@Test
	public void missingLink() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown.expectMessage(equalTo("Links with the following relations were not"
				+ " found in the response: [foo]"));
		documentLinks("undocumented-link", new StubLinkExtractor(),
				new LinkDescriptor("foo").description("bar")).handle(result());
	}

	@Test
	public void optionalLink() throws IOException {
		this.snippet.expectLinks("documented-optional-link").withContents( //
				tableWithHeader("Relation", "Description") //
						.row("foo", "bar"));
		documentLinks("documented-optional-link",
				new StubLinkExtractor().withLinks(new Link("foo", "blah")),
				new LinkDescriptor("foo").description("bar").optional()).handle(result());
	}

	@Test
	public void optionalMissingLink() throws IOException {
		this.snippet.expectLinks("documented-optional-missing-link").withContents( //
				tableWithHeader("Relation", "Description") //
						.row("foo", "bar"));
		documentLinks("documented-optional-missing-link", new StubLinkExtractor(),
				new LinkDescriptor("foo").description("bar").optional()).handle(result());
	}

	@Test
	public void undocumentedLinkAndMissingLink() throws IOException {
		this.thrown.expect(SnippetGenerationException.class);
		this.thrown.expectMessage(equalTo("Links with the following relations were not"
				+ " documented: [a]. Links with the following relations were not"
				+ " found in the response: [foo]"));
		documentLinks("undocumented-link-and-missing-link",
				new StubLinkExtractor().withLinks(new Link("a", "alpha")),
				new LinkDescriptor("foo").description("bar")).handle(result());
	}

	@Test
	public void documentedLinks() throws IOException {
		this.snippet.expectLinks("documented-links").withContents( //
				tableWithHeader("Relation", "Description") //
						.row("a", "one") //
						.row("b", "two"));
		documentLinks(
				"documented-links",
				new StubLinkExtractor().withLinks(new Link("a", "alpha"), new Link("b",
						"bravo")), new LinkDescriptor("a").description("one"),
				new LinkDescriptor("b").description("two")).handle(result());
	}

	private static class StubLinkExtractor implements LinkExtractor {

		private MultiValueMap<String, Link> linksByRel = new LinkedMultiValueMap<String, Link>();

		@Override
		public MultiValueMap<String, Link> extractLinks(MockHttpServletResponse response)
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
