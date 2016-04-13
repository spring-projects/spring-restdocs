/*
 * Copyright 2014-2016 the original author or authors.
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
import java.util.Map.Entry;

import org.springframework.http.MediaType;

/**
 * {@link LinkExtractor} that extracts links in Hypermedia Application Language (HAL)
 * format.
 *
 * @author Andy Wilkinson
 */
class HalLinkExtractor extends AbstractJsonLinkExtractor {

	static final MediaType HAL_MEDIA_TYPE = new MediaType("application", "hal+json");

	@Override
	public Map<String, List<Link>> extractLinks(Map<String, Object> json) {
		Map<String, List<Link>> extractedLinks = new LinkedHashMap<>();
		Object possibleLinks = json.get("_links");
		if (possibleLinks instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> links = (Map<String, Object>) possibleLinks;
			for (Entry<String, Object> entry : links.entrySet()) {
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
			Collection<Object> possibleLinkObjects = (Collection<Object>) object;
			for (Object possibleLinkObject : possibleLinkObjects) {
				maybeAddLink(maybeCreateLink(rel, possibleLinkObject), links);
			}
		}
		else {
			maybeAddLink(maybeCreateLink(rel, object), links);
		}
		return links;
	}

	private static Link maybeCreateLink(String rel, Object possibleLinkObject) {
		if (possibleLinkObject instanceof Map) {
			Map<?, ?> possibleLinkMap = (Map<?, ?>) possibleLinkObject;
			Object hrefObject = possibleLinkMap.get("href");
			if (hrefObject instanceof String) {
				Object titleObject = possibleLinkMap.get("title");
				return new Link(rel, (String) hrefObject,
						titleObject instanceof String ? (String) titleObject : null);
			}
		}
		return null;
	}

	private static void maybeAddLink(Link possibleLink, List<Link> links) {
		if (possibleLink != null) {
			links.add(possibleLink);
		}
	}

}
