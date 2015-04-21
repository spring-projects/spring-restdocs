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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.restdocs.hypermedia.LinkExtractors.AtomLinkExtractor;
import org.springframework.restdocs.hypermedia.LinkExtractors.HalLinkExtractor;

/**
 * Tests for {@link LinkExtractors}.
 * 
 * @author Andy Wilkinson
 */
public class LinkExtractorsTests {

	@Test
	public void nullContentTypeYieldsNullExtractor() {
		assertThat(LinkExtractors.extractorForContentType(null), nullValue());
	}

	@Test
	public void emptyContentTypeYieldsNullExtractor() {
		assertThat(LinkExtractors.extractorForContentType(""), nullValue());
	}

	@Test
	public void applicationJsonContentTypeYieldsAtomExtractor() {
		LinkExtractor linkExtractor = LinkExtractors
				.extractorForContentType("application/json");
		assertThat(linkExtractor, instanceOf(AtomLinkExtractor.class));
	}

	@Test
	public void applicationHalJsonContentTypeYieldsHalExtractor() {
		LinkExtractor linkExtractor = LinkExtractors
				.extractorForContentType("application/hal+json");
		assertThat(linkExtractor, instanceOf(HalLinkExtractor.class));
	}

	@Test
	public void contentTypeWithParameterYieldsExtractor() {
		LinkExtractor linkExtractor = LinkExtractors
				.extractorForContentType("application/json;foo=bar");
		assertThat(linkExtractor, instanceOf(AtomLinkExtractor.class));
	}

}
