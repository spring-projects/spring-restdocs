/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Abstract base class for operation requests, request parts, and responses.
 *
 * @author Andy Wilkinson
 */
abstract class AbstractOperationMessage implements OperationMessage {

	private final byte[] content;

	private final HttpHeaders headers;

	AbstractOperationMessage(byte @Nullable [] content, HttpHeaders headers) {
		this.content = (content != null) ? content : new byte[0];
		this.headers = headers;
	}

	@Override
	public byte[] getContent() {
		return Arrays.copyOf(this.content, this.content.length);
	}

	@Override
	public HttpHeaders getHeaders() {
		return HttpHeaders.readOnlyHttpHeaders(this.headers);
	}

	@Override
	public String getContentAsString() {
		if (this.content.length > 0) {
			Charset charset = extractCharsetFromContentTypeHeader();
			if (charset == null) {
				charset = StandardCharsets.UTF_8;
			}
			return new String(this.content, charset);
		}
		return "";
	}

	private @Nullable Charset extractCharsetFromContentTypeHeader() {
		if (this.headers == null) {
			return null;
		}
		MediaType contentType = this.headers.getContentType();
		if (contentType == null) {
			return null;
		}
		return contentType.getCharset();
	}

}
