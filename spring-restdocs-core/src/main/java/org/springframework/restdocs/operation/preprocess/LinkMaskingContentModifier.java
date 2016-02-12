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

package org.springframework.restdocs.operation.preprocess;

import java.util.regex.Pattern;

import org.springframework.http.MediaType;

/**
 * A content modifier the masks the {@code href} of any hypermedia links.
 *
 * @author Andy Wilkinson
 */
class LinkMaskingContentModifier implements ContentModifier {

	private static final String DEFAULT_MASK = "...";

	private static final Pattern LINK_HREF = Pattern.compile("\"href\"\\s*:\\s*\"(.*?)\"",
			Pattern.DOTALL);

	private final ContentModifier contentModifier;

	LinkMaskingContentModifier() {
		this(DEFAULT_MASK);
	}

	LinkMaskingContentModifier(String mask) {
		this.contentModifier = new PatternReplacingContentModifier(LINK_HREF, mask);
	}

	@Override
	public byte[] modifyContent(byte[] originalContent, MediaType contentType) {
		return this.contentModifier.modifyContent(originalContent, contentType);
	}

}
