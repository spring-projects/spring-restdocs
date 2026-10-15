/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpHeaders;

/**
 * Helper for working with {@link HttpHeaders}.
 *
 * @author Andy Wilkinson
 */
class HttpHeadersHelper {

	private final HttpHeaders httpHeaders;

	HttpHeadersHelper(HttpHeaders httpHeaders) {
		HttpHeaders headers = new HttpHeaders();
		if (httpHeaders != null) {
			headers.putAll(httpHeaders);
		}
		this.httpHeaders = headers;
	}

	HttpHeadersHelper addIfAbsent(String name, String value) {
		if (this.httpHeaders.get(name) == null) {
			this.httpHeaders.add(name, value);
		}
		return this;
	}

	HttpHeadersHelper updateContentLengthHeaderIfPresent(byte @Nullable [] content) {
		if (this.httpHeaders.getContentLength() != -1) {
			setContentLengthHeader(content);
		}
		return this;
	}

	HttpHeadersHelper setContentLengthHeader(byte @Nullable [] content) {
		if (content == null || content.length == 0) {
			this.httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
		}
		else {
			this.httpHeaders.setContentLength(content.length);
		}
		return this;
	}

	HttpHeaders getHeaders() {
		return HttpHeaders.readOnlyHttpHeaders(this.httpHeaders);
	}

}
