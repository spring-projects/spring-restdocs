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

import java.util.regex.Pattern;

/**
 * A {@link ResponsePostProcessor} that modifies the content of a hypermedia response to
 * mask the hrefs of any links.
 * 
 * @author Andy Wilkinson
 * @author Dewet Diener
 */
class LinkMaskingResponsePostProcessor extends PatternReplacingResponsePostProcessor {

	private static final String DEFAULT_MASK = "...";

	private static final Pattern LINK_HREF = Pattern.compile(
			"\"href\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

	LinkMaskingResponsePostProcessor() {
		super(LINK_HREF, DEFAULT_MASK);
	}

	LinkMaskingResponsePostProcessor(String mask) {
		super(LINK_HREF, mask);
	}

}
