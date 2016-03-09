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

package org.springframework.restdocs.curl;

import java.util.Map;

import org.springframework.restdocs.snippet.Snippet;

/**
 * A {@link Snippet} that documents the curl command for a request.
 *
 * @author Andy Wilkinson
 * @author Paul-Christian Volkmer
 * @author Raman Gupta
 * @deprecated Since 1.1 in favor of
 * {@link org.springframework.restdocs.cli.CurlRequestSnippet}.
 */
@Deprecated
public class CurlRequestSnippet
		extends org.springframework.restdocs.cli.CurlRequestSnippet {

	/**
	 * Creates a new {@code CurlRequestSnippet} with no additional attributes.
	 *
	 * @deprecated Since 1.1 in favor of
	 * {@link org.springframework.restdocs.cli.CurlRequestSnippet}.
	 */
	@Deprecated
	protected CurlRequestSnippet() {
		super();
	}

	/**
	 * Creates a new {@code CurlRequestSnippet} with additional attributes.
	 * @param attributes The additional attributes.
	 *
	 * @deprecated Since 1.1 in favor of
	 * {@link org.springframework.restdocs.cli.CurlRequestSnippet}.
	 */
	@Deprecated
	protected CurlRequestSnippet(final Map<String, Object> attributes) {
		super(attributes);
	}

}
