/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.hypermedia;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import tools.jackson.databind.ObjectMapper;

import org.springframework.restdocs.operation.OperationResponse;

/**
 * Abstract base class for a {@link LinkExtractor} that extracts links from JSON.
 *
 * @author Andy Wilkinson
 */
abstract class AbstractJsonLinkExtractor implements LinkExtractor {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, List<Link>> extractLinks(OperationResponse response) throws IOException {
		Map<String, Object> jsonContent = this.objectMapper.readValue(response.getContent(), Map.class);
		return extractLinks(jsonContent);
	}

	protected abstract Map<String, List<Link>> extractLinks(Map<String, Object> json);

}
