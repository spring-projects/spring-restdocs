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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.operation.OperationResponse;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

/**
 * {@link LinkExtractor} that extracts links based on a json path selector.
 *
 * @author Mattias Severson
 *
 * @see HypermediaDocumentation#jsonPathLinks(String...)
 */
class JsonPathLinkExtractor implements LinkExtractor {

	private final List<String> jsonPaths;

	JsonPathLinkExtractor(String... jsonPaths) {
		this.jsonPaths = Arrays.asList(jsonPaths);
	}

	@Override
	public Map<String, List<Link>> extractLinks(OperationResponse response)
			throws IOException {
		try (InputStream inputStream = new ByteArrayInputStream(response.getContent())) {
			Map<String, List<Link>> result = new HashMap<>();
			DocumentContext json = JsonPath.parse(inputStream);
			for (String jsonPath : this.jsonPaths) {
				try {
					Object jsonContent = json.read(jsonPath);
					result.putAll(extractLinks(jsonContent));
				}
				catch (PathNotFoundException e) {
					// ignore
				}
			}
			return result;
		}
	}

	public Map<String, List<Link>> extractLinks(Object json) {
		Map<String, List<Link>> extractedLinks = new HashMap<>();
		if (json instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> links = (Map<String, Object>) json;
			for (Map.Entry<String, Object> entry : links.entrySet()) {
				String rel = entry.getKey();
				extractedLinks.put(rel, convertToLinks(entry.getValue(), rel));
			}
		}
		return extractedLinks;
	}

	private static List<Link> convertToLinks(Object object, String rel) {
		List<Link> links = new ArrayList<>();
		if (object instanceof Collection) {
			@SuppressWarnings("unchecked")
			Collection<Object> hrefObjects = (Collection<Object>) object;
			for (Object hrefObject : hrefObjects) {
				maybeAddLink(maybeCreateLink(rel, hrefObject), links);
			}
		}
		else {
			maybeAddLink(maybeCreateLink(rel, object), links);
		}
		return links;
	}

	private static Link maybeCreateLink(String rel, Object possibleHref) {
		if (possibleHref instanceof String) {
			return new Link(rel, (String) possibleHref);
		}
		return null;
	}

	private static void maybeAddLink(Link possibleLink, List<Link> links) {
		if (possibleLink != null) {
			links.add(possibleLink);
		}
	}

}
