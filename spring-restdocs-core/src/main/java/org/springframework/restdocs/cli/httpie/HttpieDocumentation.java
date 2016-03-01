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

package org.springframework.restdocs.cli.httpie;

import java.util.Map;

import org.springframework.restdocs.snippet.Snippet;

/**
 * Static factory methods for documenting a RESTful API as if it were being driven using
 * the httpie command-line utility.
 *
 * @author Andy Wilkinson
 * @author Paul-Christian Volkmer
 * @author Raman Gupta
 */
public abstract class HttpieDocumentation {

	private HttpieDocumentation() {

	}

	/**
	 * Returns a new {@code Snippet} that will document the httpie request for the API
	 * operation.
	 *
	 * @return the snippet that will document the httpie request
	 */
	public static Snippet httpieRequest() {
		return new HttpieRequestSnippet();
	}

	/**
	 * Returns a new {@code Snippet} that will document the httpie request for the API
	 * operation. The given {@code attributes} will be available during snippet
	 * generation.
	 *
	 * @param attributes the attributes
	 * @return the snippet that will document the httpie request
	 */
	public static Snippet httpieRequest(Map<String, Object> attributes) {
		return new HttpieRequestSnippet(attributes);
	}

}
