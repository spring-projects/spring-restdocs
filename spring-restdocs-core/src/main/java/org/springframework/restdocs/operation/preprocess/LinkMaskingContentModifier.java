/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.jspecify.annotations.Nullable;

import org.springframework.http.MediaType;

/**
 * A content modifier the masks the {@code href} of any hypermedia links.
 *
 * @author Andy Wilkinson
 */
class LinkMaskingContentModifier implements ContentModifier {

	private static final String DEFAULT_MASK = "...";

	private static final Pattern LINK_HREF = Pattern.compile("\"href\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

	private final ContentModifier contentModifier;

	LinkMaskingContentModifier() {
		this(DEFAULT_MASK);
	}

	LinkMaskingContentModifier(String mask) {
		this.contentModifier = new PatternReplacingContentModifier(LINK_HREF, mask, StandardCharsets.UTF_8);
	}

	@Override
	public byte[] modifyContent(byte[] originalContent, @Nullable MediaType contentType) {
		return this.contentModifier.modifyContent(originalContent, contentType);
	}

}
