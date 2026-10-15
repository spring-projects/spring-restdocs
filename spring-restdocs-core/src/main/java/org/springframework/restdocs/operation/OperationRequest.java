/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import java.net.URI;
import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * The request that was sent as part of performing an operation on a RESTful service.
 *
 * @author Andy Wilkinson
 * @see Operation#getRequest()
 */
public interface OperationRequest {

	/**
	 * Returns the content of the request. If the request has no content an empty array is
	 * returned.
	 * @return the contents, never {@code null}
	 */
	byte[] getContent();

	/**
	 * Returns the content of the request as a {@link String}. If the request has no
	 * content an empty string is returned. If the request has a {@code Content-Type}
	 * header that specifies a charset then that charset will be used when converting the
	 * contents to a {@code String}.
	 * @return the contents as string, never {@code null}
	 */
	String getContentAsString();

	/**
	 * Returns the headers that were included in the request.
	 * @return the headers
	 */
	HttpHeaders getHeaders();

	/**
	 * Returns the HTTP method of the request.
	 * @return the HTTP method
	 */
	HttpMethod getMethod();

	/**
	 * Returns the request's parts, provided that it is a multipart request. If not, then
	 * an empty {@link Collection} is returned.
	 * @return the parts
	 */
	Collection<OperationRequestPart> getParts();

	/**
	 * Returns the request's URI.
	 * @return the URI
	 */
	URI getUri();

	/**
	 * Returns the {@link RequestCookie cookies} sent with the request. If no cookies were
	 * sent an empty collection is returned.
	 * @return the cookies, never {@code null}
	 * @since 1.2.0
	 */
	Collection<RequestCookie> getCookies();

}
