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

package org.springframework.restdocs.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Static factory methods provided a selection of {@link LinkExtractor link extractors}
 * for use when documentating a hypermedia-based API.
 * 
 * @author Andy Wilkinson
 *
 */
public class LinkExtractors {

	/**
	 * Returns a {@code LinkExtractor} capable of extracting links in Hypermedia
	 * Application Language (HAL) format where the links are found in a map named
	 * {@code _links}.
	 * 
	 * @return The extract for HAL-style links
	 */
	public static LinkExtractor halLinks() {
		return new HalLinkExtractor();
	}

	/**
	 * Returns a {@code LinkExtractor} capable of extracting links in Atom format where
	 * the links are found in an array named {@code links}.
	 * 
	 * @return The extractor for Atom-style links
	 */
	public static LinkExtractor atomLinks() {
		return new AtomLinkExtractor();
	}

	private static abstract class JsonContentLinkExtractor implements LinkExtractor {

		private final ObjectMapper objectMapper = new ObjectMapper();

		@SuppressWarnings("unchecked")
		public Map<String, List<Link>> extractLinks(MockHttpServletResponse response)
				throws IOException {
			Map<String, Object> jsonContent = this.objectMapper.readValue(
					response.getContentAsString(), Map.class);
			return extractLinks(jsonContent);
		}

		protected abstract Map<String, List<Link>> extractLinks(Map<String, Object> json);
	}

	private static class HalLinkExtractor extends JsonContentLinkExtractor {

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, List<Link>> extractLinks(Map<String, Object> json) {
			Map<String, List<Link>> extractedLinks = new HashMap<>();
			Object possibleLinks = json.get("_links");
			if (possibleLinks instanceof Map) {
				Map<String, Object> links = (Map<String, Object>) possibleLinks;
				for (Entry<String, Object> entry : links.entrySet()) {
					String rel = (String) entry.getKey();
					extractedLinks.put(rel, convertToLinks(entry.getValue(), rel));
				}
			}
			return extractedLinks;
		}

		@SuppressWarnings("unchecked")
		private static List<Link> convertToLinks(Object object, String rel) {
			List<Link> links = new ArrayList<>();
			if (object instanceof Collection) {
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

	private static class AtomLinkExtractor extends JsonContentLinkExtractor {

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, List<Link>> extractLinks(Map<String, Object> json) {
			Map<String, List<Link>> extractedLinks = new HashMap<>();
			Object possibleLinks = json.get("links");
			if (possibleLinks instanceof Collection) {
				Collection<Object> linksCollection = (Collection<Object>) possibleLinks;
				for (Object linkObject : linksCollection) {
					if (linkObject instanceof Map) {
						Link link = maybeCreateLink((Map<String, Object>) linkObject);
						maybeStoreLink(link, extractedLinks);
					}
				}
			}
			return extractedLinks;
		}

		private static Link maybeCreateLink(Map<String, Object> linkMap) {
			Object hrefObject = linkMap.get("href");
			Object relObject = linkMap.get("rel");
			if (relObject instanceof String && hrefObject instanceof String) {
				return new Link((String) relObject, (String) hrefObject);
			}
			return null;
		}

		private static void maybeStoreLink(Link link,
				Map<String, List<Link>> extractedLinks) {
			if (link != null) {
				List<Link> linksForRel = extractedLinks.get(link.getRel());
				if (linksForRel == null) {
					linksForRel = new ArrayList<Link>();
					extractedLinks.put(link.getRel(), linksForRel);
				}
				linksForRel.add(link);
			}
		}
	}
}
