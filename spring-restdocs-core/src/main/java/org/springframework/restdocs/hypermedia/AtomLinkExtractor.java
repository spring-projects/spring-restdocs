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

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

	private static Link maybeCreateLink(Map<String, Object> linkMap) {
		Object hrefObject = linkMap.get("href");
		Object relObject = linkMap.get("rel");
		if (relObject instanceof String && hrefObject instanceof String) {
			Object titleObject = linkMap.get("title");
			return new Link((String) relObject, (String) hrefObject,
					titleObject instanceof String ? (String) titleObject : null);
		}
		return null;
	}

	private static void maybeStoreLink(Link link,
			MultiValueMap<String, Link> extractedLinks) {
		if (link != null) {
			extractedLinks.add(link.getRel(), link);
		}
	}

}
