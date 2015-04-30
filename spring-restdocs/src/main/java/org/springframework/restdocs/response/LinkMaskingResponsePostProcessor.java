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

package org.springframework.restdocs.response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LinkMaskingResponsePostProcessor extends ContentModifyingReponsePostProcessor {

	private static final String DEFAULT_MASK = "...";

	private static final Pattern LINK_HREF = Pattern.compile(
			"\"href\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

	private final String mask;

	LinkMaskingResponsePostProcessor() {
		this(DEFAULT_MASK);
	}

	LinkMaskingResponsePostProcessor(String mask) {
		this.mask = mask;
	}

	@Override
	protected String modifyContent(String originalContent) {
		Matcher matcher = LINK_HREF.matcher(originalContent);
		StringBuilder buffer = new StringBuilder();
		int previous = 0;
		while (matcher.find()) {
			buffer.append(originalContent.substring(previous, matcher.start(1)));
			buffer.append(this.mask);
			previous = matcher.end(1);
		}
		if (previous < originalContent.length()) {
			buffer.append(originalContent.substring(previous));
		}
		return buffer.toString();
	}

}
