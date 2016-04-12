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

package org.springframework.restdocs.headers;

import java.util.Arrays;
import java.util.Map;

import org.springframework.restdocs.snippet.Snippet;

/**
 * Static factory methods for documenting a RESTful API's request and response headers.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 */
public abstract class HeaderDocumentation {

	private HeaderDocumentation() {

	}

	/**
	 * Creates a {@code HeaderDescriptor} that describes a header with the given
	 * {@code name}.
	 *
	 * @param name The name of the header
	 * @return a {@code HeaderDescriptor} ready for further configuration
	 */
	public static HeaderDescriptor headerWithName(String name) {
		return new HeaderDescriptor(name);
	}

	/**
	 * Returns a new {@link Snippet} that will document the headers of the API operation's
	 * request. The headers will be documented using the given {@code descriptors}.
	 * <p>
	 * If a header is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur.
	 *
	 * @param descriptors the descriptions of the request's headers
	 * @return the snippet that will document the request headers
	 * @see #headerWithName(String)
	 */
	public static RequestHeadersSnippet requestHeaders(HeaderDescriptor... descriptors) {
		return new RequestHeadersSnippet(Arrays.asList(descriptors));
	}

	/**
	 * Returns a new {@link Snippet} that will document the headers of the API
	 * operations's request. The given {@code attributes} will be available during snippet
	 * generation and the headers will be documented using the given {@code descriptors}.
	 * <p>
	 * If a header is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's headers
	 * @return the snippet that will document the request headers
	 * @see #headerWithName(String)
	 */
	public static RequestHeadersSnippet requestHeaders(Map<String, Object> attributes,
			HeaderDescriptor... descriptors) {
		return new RequestHeadersSnippet(Arrays.asList(descriptors), attributes);
	}

	/**
	 * Returns a new {@link Snippet} that will document the headers of the API operation's
	 * response. The headers will be documented using the given {@code descriptors}.
	 * <p>
	 * If a header is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur.
	 *
	 * @param descriptors the descriptions of the response's headers
	 * @return the snippet that will document the response headers
	 * @see #headerWithName(String)
	 */
	public static ResponseHeadersSnippet responseHeaders(
			HeaderDescriptor... descriptors) {
		return new ResponseHeadersSnippet(Arrays.asList(descriptors));
	}

	/**
	 * Returns a new {@link Snippet} that will document the headers of the API
	 * operations's response. The given {@code attributes} will be available during
	 * snippet generation and the headers will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a header is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response's headers
	 * @return the snippet that will document the response headers
	 * @see #headerWithName(String)
	 */
	public static ResponseHeadersSnippet responseHeaders(Map<String, Object> attributes,
			HeaderDescriptor... descriptors) {
		return new ResponseHeadersSnippet(Arrays.asList(descriptors), attributes);
	}

}
