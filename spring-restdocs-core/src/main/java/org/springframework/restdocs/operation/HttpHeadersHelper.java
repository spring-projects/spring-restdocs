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

	HttpHeadersHelper updateContentLengthHeaderIfPresent(byte[] content) {
		if (this.httpHeaders.getContentLength() != -1) {
			setContentLengthHeader(content);
		}
		return this;
	}

	HttpHeadersHelper setContentLengthHeader(byte[] content) {
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
