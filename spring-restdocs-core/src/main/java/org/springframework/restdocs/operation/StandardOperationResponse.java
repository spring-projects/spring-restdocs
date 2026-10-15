/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import java.util.Collection;

import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * Standard implementation of {@link OperationResponse}.
 *
 * @author Andy Wilkinson
 * @author Clyde Stubbs
 */
class StandardOperationResponse extends AbstractOperationMessage implements OperationResponse {

	private final HttpStatusCode status;

	private Collection<ResponseCookie> cookies;

	/**
	 * Creates a new response with the given {@code status}, {@code headers}, and
	 * {@code content}.
	 * @param status the status of the response
	 * @param headers the headers of the response
	 * @param content the content of the response
	 * @param cookies any cookies included in the response
	 */
	StandardOperationResponse(HttpStatusCode status, HttpHeaders headers, byte @Nullable [] content,
			Collection<ResponseCookie> cookies) {
		super(content, headers);
		this.status = status;
		this.cookies = cookies;
	}

	@Override
	public HttpStatusCode getStatus() {
		return this.status;
	}

	@Override
	public Collection<ResponseCookie> getCookies() {
		return this.cookies;
	}

}
