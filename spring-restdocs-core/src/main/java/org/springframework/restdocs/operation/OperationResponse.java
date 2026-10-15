/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * The response that was received as part of performing an operation on a RESTful service.
 *
 * @author Andy Wilkinson
 * @author Clyde Stubbs
 * @see Operation
 * @see Operation#getRequest()
 */
public interface OperationResponse {

	/**
	 * Returns the status of the response.
	 * @return the status, never {@code null}
	 */
	HttpStatusCode getStatus();

	/**
	 * Returns the headers in the response.
	 * @return the headers
	 */
	HttpHeaders getHeaders();

	/**
	 * Returns the content of the response. If the response has no content an empty array
	 * is returned.
	 * @return the contents, never {@code null}
	 */
	byte[] getContent();

	/**
	 * Returns the content of the response as a {@link String}. If the response has no
	 * content an empty string is returned. If the response has a {@code Content-Type}
	 * header that specifies a charset then that charset will be used when converting the
	 * contents to a {@code String}.
	 * @return the contents as string, never {@code null}
	 */
	String getContentAsString();

	/**
	 * Returns the {@link ResponseCookie cookies} returned with the response. If no
	 * cookies were returned an empty collection is returned.
	 * @return the cookies, never {@code null}
	 * @since 3.0
	 */
	Collection<ResponseCookie> getCookies();

}
