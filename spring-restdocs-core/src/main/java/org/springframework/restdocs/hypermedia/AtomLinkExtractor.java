/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.hypermedia;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * {@link LinkExtractor} that extracts links in Atom format.
 *
 * @author Andy Wilkinson
 */
@SuppressWarnings("unchecked")
class AtomLinkExtractor extends AbstractJsonLinkExtractor {

	@Override
	public Map<String, List<Link>> extractLinks(Map<String, Object> json) {
		MultiValueMap<String, Link> extractedLinks = new LinkedMultiValueMap<>();
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

	private static @Nullable Link maybeCreateLink(Map<String, Object> linkMap) {
		Object hrefObject = linkMap.get("href");
		Object relObject = linkMap.get("rel");
		if (relObject instanceof String && hrefObject instanceof String) {
			Object titleObject = linkMap.get("title");
			return new Link((String) relObject, (String) hrefObject,
					(titleObject instanceof String) ? (String) titleObject : null);
		}
		return null;
	}

	private static void maybeStoreLink(@Nullable Link link, MultiValueMap<String, Link> extractedLinks) {
		if (link != null) {
			extractedLinks.add(link.getRel(), link);
		}
	}

}
