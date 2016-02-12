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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	public Map<String, List<Link>> extractLinks(OperationResponse response)
			throws IOException {
		Map<String, Object> jsonContent = this.objectMapper
				.readValue(response.getContent(), Map.class);
		return extractLinks(jsonContent);
	}

	protected abstract Map<String, List<Link>> extractLinks(Map<String, Object> json);

}
