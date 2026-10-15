/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.hypermedia;

import java.io.IOException;

import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Stub implementation of {@code LinkExtractor} for testing.
 *
 * @author Andy Wilkinson
 */
class StubLinkExtractor implements LinkExtractor {

	private MultiValueMap<String, Link> linksByRel = new LinkedMultiValueMap<>();

	@Override
	public MultiValueMap<String, Link> extractLinks(OperationResponse response) throws IOException {
		return this.linksByRel;
	}

	StubLinkExtractor withLinks(Link... links) {
		for (Link link : links) {
			this.linksByRel.add(link.getRel(), link);
		}
		return this;
	}

}
