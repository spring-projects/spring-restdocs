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


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link LinkExtractor} that extracts links based on a dynamic json path selector.
 *
 * @author Mattias Severson
 *
 * @see HypermediaDocumentation#dynamicJsonPathLinks(String...)
 */
class DynamicJsonPathLinkExtractor extends AbstractJsonLinkExtractor {

	private final List<String> jsonPaths;

	DynamicJsonPathLinkExtractor(List<String> jsonPaths) {
		this.jsonPaths = jsonPaths;
	}

	@Override
	public Map<String, List<Link>> extractLinks(Map<String, Object> json) {
		Map<String, List<Link>> extractedLinks = new LinkedHashMap<>();
		for (String jsonPath : this.jsonPaths) {
			Object possibleLinks = getPossibleLinks(jsonPath, json);
			if (possibleLinks instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> links = (Map<String, Object>) possibleLinks;
				for (Map.Entry<String, Object> entry : links.entrySet()) {
					String rel = entry.getKey();
					extractedLinks.put(rel, convertToLinks(entry.getValue(), rel));
				}
			}
		}
		return extractedLinks;
	}

	private static Object getPossibleLinks(String jsonPath, Object json) {
		if (json instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> jsonDocument = (Map<String, Object>) json;
			if (!jsonPath.contains(".")) {
				return jsonDocument.get(jsonPath);
			}
			else {
				int dotIndex = jsonPath.indexOf(".");
				String jsonKey = jsonPath.substring(0, dotIndex);
				return getPossibleLinks(jsonPath.substring(dotIndex + 1), jsonDocument.get(jsonKey));
			}
		}
		return json;
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
