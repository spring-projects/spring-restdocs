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

package org.springframework.restdocs.operation;

import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Standard implementation of {@link OperationResponse}.
 *
 * @author Andy Wilkinson
 */
public class StandardOperationResponse implements OperationResponse {

	private final HttpStatus status;

	private final HttpHeaders headers;

	private final byte[] content;

	private String characterEncoding;

	/**
	 * Creates a new response with the given {@code status}, {@code headers}, and
	 * {@code content}.
	 *
	 * @param status the status of the response
	 * @param headers the headers of the response
	 * @param content the content of the response
	 */
	public StandardOperationResponse(HttpStatus status, HttpHeaders headers,
			byte[] content) {
		this.status = status;
		this.headers = headers;
		this.content = content;
		this.characterEncoding = detectCharsetFromContentTypeHeader(headers);
	}

	@Override
	public HttpStatus getStatus() {
		return this.status;
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.headers;
	}

	@Override
	public byte[] getContent() {
		return this.content;
	}

	@Override
	public String getContentAsString() throws UnsupportedEncodingException {
		if (content.length > 0) {
			return characterEncoding != null ?
					new String(content, characterEncoding) : new String(content);
		}
		else {
			return "";
		}
	}

	private String detectCharsetFromContentTypeHeader(HttpHeaders headers) {
		if (headers == null) {
			return null;
		}
		MediaType contentType = headers.getContentType();
		if (contentType == null) {
			return null;
		}
		return contentType.getParameter("charset");
	}
}
