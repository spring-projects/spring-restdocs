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
 * Static factory methods for documenting a RESTful API as if it were being driven using
 * the cURL command-line utility.
 *
 * @deprecated Since 1.1 in favor of
 * {@link org.springframework.restdocs.cli.CliDocumentation}.
 * @author Andy Wilkinson
 * @author Yann Le Guern
 * @author Dmitriy Mayboroda
 * @author Jonathan Pearlin
 */
@Deprecated
public abstract class CurlDocumentation {

	private CurlDocumentation() {

	}

	/**
	 * Returns a new {@code Snippet} that will document the curl request for the API
	 * operation.
	 *
	 * @return the snippet that will document the curl request
	 *
	 * @deprecated Since 1.1 in favor of
	 * {@link org.springframework.restdocs.cli.CliDocumentation#curlRequest()}.
	 */
	@Deprecated
	public static Snippet curlRequest() {
		return new CurlRequestSnippet();
	}

	/**
	 * Returns a new {@code Snippet} that will document the curl request for the API
	 * operation. The given {@code attributes} will be available during snippet
	 * generation.
	 *
	 * @param attributes the attributes
	 * @return the snippet that will document the curl request
	 *
	 * @deprecated Since 1.1 in favor of
	 * {@link org.springframework.restdocs.cli.CliDocumentation#curlRequest(Map)}.
	 */
	@Deprecated
	public static Snippet curlRequest(Map<String, Object> attributes) {
		return new CurlRequestSnippet(attributes);
	}

}
