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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationResponse;

/**
 * {@link LinkExtractor} that delegates to other link extractors based on the response's
 * content type.
 *
 * @author Andy Wilkinson
 *
 */
class ContentTypeLinkExtractor implements LinkExtractor {

	private Map<MediaType, LinkExtractor> linkExtractors = new HashMap<>();

	ContentTypeLinkExtractor() {
		this.linkExtractors.put(MediaType.APPLICATION_JSON, new AtomLinkExtractor());
		this.linkExtractors.put(HalLinkExtractor.HAL_MEDIA_TYPE, new HalLinkExtractor());
	}

	ContentTypeLinkExtractor(Map<MediaType, LinkExtractor> linkExtractors) {
		this.linkExtractors.putAll(linkExtractors);
	}

	@Override
	public Map<String, List<Link>> extractLinks(OperationResponse response)
			throws IOException {
		MediaType contentType = response.getHeaders().getContentType();
		LinkExtractor extractorForContentType = getExtractorForContentType(contentType);
		if (extractorForContentType != null) {
			return extractorForContentType.extractLinks(response);
		}
		throw new IllegalStateException(
				"No LinkExtractor has been provided and one is not available for the "
						+ "content type " + contentType);
	}

	private LinkExtractor getExtractorForContentType(MediaType contentType) {
		if (contentType != null) {
			for (Entry<MediaType, LinkExtractor> entry : this.linkExtractors.entrySet()) {
				if (contentType.isCompatibleWith(entry.getKey())) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

}
