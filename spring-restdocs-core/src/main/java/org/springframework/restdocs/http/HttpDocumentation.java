/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.http;

import java.util.Map;

/**
 * Static factory methods for documenting a RESTful API's HTTP requests.
 *
 * @author Andy Wilkinson
 * @author Jonathan Pearlin
 */
public abstract class HttpDocumentation {

	private HttpDocumentation() {

	}

	/**
	 * Returns a new {@code Snippet} that will document the HTTP request for the API
	 * operation.
	 * @return the snippet that will document the HTTP request
	 */
	public static HttpRequestSnippet httpRequest() {
		return new HttpRequestSnippet();
	}

	/**
	 * Returns a new {@code Snippet} that will document the HTTP request for the API
	 * operation. The given {@code attributes} will be available during snippet
	 * generation.
	 * @param attributes the attributes
	 * @return the snippet that will document the HTTP request
	 */
	public static HttpRequestSnippet httpRequest(Map<String, Object> attributes) {
		return new HttpRequestSnippet(attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the HTTP response for the API
	 * operation.
	 * @return the snippet that will document the HTTP response
	 */
	public static HttpResponseSnippet httpResponse() {
		return new HttpResponseSnippet();
	}

	/**
	 * Returns a {@code Snippet} that will document the HTTP response for the API
	 * operation. The given {@code attributes} will be available during snippet
	 * generation.
	 * @param attributes the attributes
	 * @return the snippet that will document the HTTP response
	 */
	public static HttpResponseSnippet httpResponse(Map<String, Object> attributes) {
		return new HttpResponseSnippet(attributes);
	}

}
