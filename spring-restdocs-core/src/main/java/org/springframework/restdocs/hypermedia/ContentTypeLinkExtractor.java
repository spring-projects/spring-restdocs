/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.hypermedia;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jspecify.annotations.Nullable;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationResponse;

/**
 * {@link LinkExtractor} that delegates to other link extractors based on the response's
 * content type.
 *
 * @author Andy Wilkinson
 * @author Oliver Drotbohm
 */
class ContentTypeLinkExtractor implements LinkExtractor {

	private Map<MediaType, LinkExtractor> linkExtractors = new HashMap<>();

	ContentTypeLinkExtractor() {
		this.linkExtractors.put(MediaType.APPLICATION_JSON, new AtomLinkExtractor());
		LinkExtractor halLinkExtractor = new HalLinkExtractor();
		this.linkExtractors.put(HalLinkExtractor.HAL_MEDIA_TYPE, halLinkExtractor);
		this.linkExtractors.put(HalLinkExtractor.VND_HAL_MEDIA_TYPE, halLinkExtractor);
		this.linkExtractors.put(HalLinkExtractor.HAL_FORMS_MEDIA_TYPE, halLinkExtractor);
	}

	ContentTypeLinkExtractor(Map<MediaType, LinkExtractor> linkExtractors) {
		this.linkExtractors.putAll(linkExtractors);
	}

	@Override
	public Map<String, List<Link>> extractLinks(OperationResponse response) throws IOException {
		MediaType contentType = response.getHeaders().getContentType();
		LinkExtractor extractorForContentType = getExtractorForContentType(contentType);
		if (extractorForContentType != null) {
			return extractorForContentType.extractLinks(response);
		}
		throw new IllegalStateException(
				"No LinkExtractor has been provided and one is not available for the " + "content type " + contentType);
	}

	private @Nullable LinkExtractor getExtractorForContentType(@Nullable MediaType contentType) {
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
