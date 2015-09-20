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

package org.springframework.restdocs.headers;

import java.util.Arrays;
import java.util.Map;

import org.springframework.restdocs.snippet.Snippet;

/**
 * Static factory methods for documenting a RESTful API's request and response headers.
 *
 * @author Andreas Evers
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
	 * Returns a handler that will produce a snippet documenting the headers of the API
	 * call's request.
	 * <p>
	 * If a header is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. If a header is present in the request, but is not
	 * documented by one of the descriptors, there will be no failure.
	 *
	 * @param descriptors The descriptions of the request's headers
	 * @return the handler
	 * @see #headerWithName(String)
	 */
	public static Snippet requestHeaders(HeaderDescriptor... descriptors) {
		return new RequestHeadersSnippet(Arrays.asList(descriptors));
	}

	/**
	 * Returns a handler that will produce a snippet documenting the headers of the API
	 * call's request. The given {@code attributes} will be available during snippet
	 * generation.
	 * <p>
	 * If a header is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. If a header is present in the request, but is not
	 * documented by one of the descriptors, there will be no failure.
	 *
	 * @param attributes Attributes made available during rendering of the snippet
	 * @param descriptors The descriptions of the request's headers
	 * @return the handler
	 * @see #headerWithName(String)
	 */
	public static Snippet requestHeaders(Map<String, Object> attributes,
			HeaderDescriptor... descriptors) {
		return new RequestHeadersSnippet(Arrays.asList(descriptors), attributes);
	}

	/**
	 * Returns a handler that will produce a snippet documenting the headers of the API
	 * call's response.
	 * <p>
	 * If a header is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. If a header is present in the response, but is not
	 * documented by one of the descriptors, there will be no failure.
	 *
	 * @param descriptors The descriptions of the response's headers
	 * @return the handler
	 * @see #headerWithName(String)
	 */
	public static Snippet responseHeaders(HeaderDescriptor... descriptors) {
		return new ResponseHeadersSnippet(Arrays.asList(descriptors));
	}

	/**
	 * Returns a handler that will produce a snippet documenting the headers of the API
	 * call's response. The given {@code attributes} will be available during snippet
	 * generation.
	 * <p>
	 * If a header is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. If a header is present in the response, but is not
	 * documented by one of the descriptors, there will be no failure.
	 *
	 * @param attributes Attributes made available during rendering of the snippet
	 * @param descriptors The descriptions of the response's headers
	 * @return the handler
	 * @see #headerWithName(String)
	 */
	public static Snippet responseHeaders(Map<String, Object> attributes,
			HeaderDescriptor... descriptors) {
		return new ResponseHeadersSnippet(Arrays.asList(descriptors), attributes);
	}

}
