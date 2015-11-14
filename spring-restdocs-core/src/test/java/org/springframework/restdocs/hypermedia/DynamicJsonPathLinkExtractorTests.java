/*
 * Copyright 2015-2015 the original author or authors.
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link DynamicJsonPathLinkExtractor} with various payloads.
 *
 * @author Mattias Severson
 *
 * @see LinkExtractorsPayloadTests
 */
public class DynamicJsonPathLinkExtractorTests {

	private final OperationResponseFactory responseFactory = new OperationResponseFactory();
	private LinkExtractor linkExtractor;

	@Test
	public void linkInSubDocument() throws IOException {
		this.linkExtractor = new DynamicJsonPathLinkExtractor(Collections.singletonList("foo.links"));
		Map<String, List<Link>> links = this.linkExtractor
				.extractLinks(createResponse("link-in-sub-document"));
		assertLinks(Collections.singletonList(new Link("alpha", "http://alpha.example.com")), links);
	}

	@Test
	public void multipleLinksInDifferentDocuments() throws IOException {
		this.linkExtractor = new DynamicJsonPathLinkExtractor(Arrays.asList("first.links", "second.links"));
		Map<String, List<Link>> links = this.linkExtractor
				.extractLinks(createResponse("multiple-links-different-sub-documents"));
		assertLinks(Arrays.asList(new Link("alpha", "http://alpha.example.com"),
				new Link("bravo", "http://bravo.example.com")), links);
	}

	private void assertLinks(List<Link> expectedLinks, Map<String, List<Link>> actualLinks) {
		MultiValueMap<String, Link> expectedLinksByRel = new LinkedMultiValueMap<>();
		for (Link expectedLink : expectedLinks) {
			expectedLinksByRel.add(expectedLink.getRel(), expectedLink);
		}
		assertEquals(expectedLinksByRel, actualLinks);
	}

	private OperationResponse createResponse(String contentName) throws IOException {
		return this.responseFactory.create(HttpStatus.OK, null,
				FileCopyUtils.copyToByteArray(getPayloadFile(contentName)));
	}

	private File getPayloadFile(String name) {
		return new File("src/test/resources/link-payloads/dynamic-json-path/" + name + ".json");
	}
}
