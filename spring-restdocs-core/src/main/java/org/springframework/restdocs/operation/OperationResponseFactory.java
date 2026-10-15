/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import java.util.Collection;
import java.util.Collections;

import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * A factory for creating {@link OperationResponse OperationResponses}.
 *
 * @author Andy Wilkinson
 * @author Clyde Stubbs
 */
public class OperationResponseFactory {

	/**
	 * Creates a new {@link OperationResponse} without cookies. If the response has any
	 * content, the given {@code headers} will be augmented to ensure that they include a
	 * {@code Content-Length} header.
	 * @param status the status of the response
	 * @param headers the request's headers
	 * @param content the content of the request
	 * @return the {@code OperationResponse}
	 * @since 3.0.0
	 */
	public OperationResponse create(HttpStatusCode status, HttpHeaders headers, byte @Nullable [] content) {
		return new StandardOperationResponse(status, augmentHeaders(headers, content), content,
				Collections.emptyList());
	}

	/**
	 * Creates a new {@link OperationResponse}. If the response has any content, the given
	 * {@code headers} will be augmented to ensure that they include a
	 * {@code Content-Length} header.
	 * @param status the status of the response
	 * @param headers the request's headers
	 * @param content the content of the request
	 * @param cookies the cookies
	 * @return the {@code OperationResponse}
	 * @since 3.0.0
	 */
	public OperationResponse create(HttpStatusCode status, HttpHeaders headers, byte @Nullable [] content,
			Collection<ResponseCookie> cookies) {
		return new StandardOperationResponse(status, augmentHeaders(headers, content), content, cookies);
	}

	/**
	 * Creates a new {@code OperationResponse} based on the given {@code original} but
	 * with the given {@code newContent}. If the original response had a
	 * {@code Content-Length} header it will be modified to match the length of the new
	 * content.
	 * @param original the original response
	 * @param newContent the new content
	 * @return the new response with the new content
	 */
	public OperationResponse createFrom(OperationResponse original, byte[] newContent) {
		return new StandardOperationResponse(original.getStatus(), getUpdatedHeaders(original.getHeaders(), newContent),
				newContent, original.getCookies());
	}

	/**
	 * Creates a new {@code OperationResponse} based on the given {@code original} but
	 * with the given {@code newHeaders}.
	 * @param original the original response
	 * @param newHeaders the new headers
	 * @return the new response with the new headers
	 */
	public OperationResponse createFrom(OperationResponse original, HttpHeaders newHeaders) {
		return new StandardOperationResponse(original.getStatus(), newHeaders, original.getContent(),
				original.getCookies());
	}

	private HttpHeaders augmentHeaders(HttpHeaders originalHeaders, byte @Nullable [] content) {
		return new HttpHeadersHelper(originalHeaders).setContentLengthHeader(content).getHeaders();
	}

	private HttpHeaders getUpdatedHeaders(HttpHeaders originalHeaders, byte[] updatedContent) {
		return new HttpHeadersHelper(originalHeaders).updateContentLengthHeaderIfPresent(updatedContent).getHeaders();
	}

}
