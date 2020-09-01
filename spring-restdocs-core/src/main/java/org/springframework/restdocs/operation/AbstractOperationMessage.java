/*
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.operation;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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

	AbstractOperationMessage(byte[] content, HttpHeaders headers) {
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

	private Charset extractCharsetFromContentTypeHeader() {
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
