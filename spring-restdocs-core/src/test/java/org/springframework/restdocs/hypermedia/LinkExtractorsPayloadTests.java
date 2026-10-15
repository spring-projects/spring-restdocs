/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.hypermedia;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link HalLinkExtractor} and {@link AtomLinkExtractor} with various payloads.
 *
 * @author Andy Wilkinson
 */
@ParameterizedClass(name = "{1}")
@MethodSource("parameters")
class LinkExtractorsPayloadTests {

	private final OperationResponseFactory responseFactory = new OperationResponseFactory();

	private final LinkExtractor linkExtractor;

	private final String linkType;

	static Collection<Object[]> parameters() {
		return Arrays.asList(new Object[] { new HalLinkExtractor(), "hal" },
				new Object[] { new AtomLinkExtractor(), "atom" });
	}

	LinkExtractorsPayloadTests(LinkExtractor linkExtractor, String linkType) {
		this.linkExtractor = linkExtractor;
		this.linkType = linkType;
	}

	@Test
	void singleLink() throws IOException {
		Map<String, List<Link>> links = this.linkExtractor.extractLinks(createResponse("single-link"));
		assertLinks(Arrays.asList(new Link("alpha", "https://alpha.example.com", "Alpha")), links);
	}

	@Test
	void multipleLinksWithDifferentRels() throws IOException {
		Map<String, List<Link>> links = this.linkExtractor
			.extractLinks(createResponse("multiple-links-different-rels"));
		assertLinks(Arrays.asList(new Link("alpha", "https://alpha.example.com", "Alpha"),
				new Link("bravo", "https://bravo.example.com")), links);
	}

	@Test
	void multipleLinksWithSameRels() throws IOException {
		Map<String, List<Link>> links = this.linkExtractor.extractLinks(createResponse("multiple-links-same-rels"));
		assertLinks(Arrays.asList(new Link("alpha", "https://alpha.example.com/one", "Alpha one"),
				new Link("alpha", "https://alpha.example.com/two")), links);
	}

	@Test
	void noLinks() throws IOException {
		Map<String, List<Link>> links = this.linkExtractor.extractLinks(createResponse("no-links"));
		assertLinks(Collections.<Link>emptyList(), links);
	}

	@Test
	void linksInTheWrongFormat() throws IOException {
		Map<String, List<Link>> links = this.linkExtractor.extractLinks(createResponse("wrong-format"));
		assertLinks(Collections.<Link>emptyList(), links);
	}

	private void assertLinks(List<Link> expectedLinks, Map<String, List<Link>> actualLinks) {
		MultiValueMap<String, Link> expectedLinksByRel = new LinkedMultiValueMap<>();
		for (Link expectedLink : expectedLinks) {
			expectedLinksByRel.add(expectedLink.getRel(), expectedLink);
		}
		assertThat(actualLinks).isEqualTo(expectedLinksByRel);
	}

	private OperationResponse createResponse(String contentName) throws IOException {
		return this.responseFactory.create(HttpStatus.OK, new HttpHeaders(),
				FileCopyUtils.copyToByteArray(getPayloadFile(contentName)));
	}

	private File getPayloadFile(String name) {
		return new File("src/test/resources/link-payloads/" + this.linkType + "/" + name + ".json");
	}

}
