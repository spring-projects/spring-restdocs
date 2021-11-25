/*
 * Copyright 2014-2021 the original author or authors.
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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * A factory for creating {@link OperationResponse OperationResponses}.
 *
 * @author Andy Wilkinson
 */
public class OperationResponseFactory {

	/**
	 * Creates a new {@link OperationResponse}. If the response has any content, the given
	 * {@code headers} will be augmented to ensure that they include a
	 * {@code Content-Length} header.
	 * @param status the status of the response
	 * @param headers the response's headers
	 * @param content the content of the response
	 * @return the {@code OperationResponse}
	 * @deprecated since 2.0.4 in favor of {@link #create(int, HttpHeaders, byte[])}
	 */
	@Deprecated
	public OperationResponse create(HttpStatus status, HttpHeaders headers, byte[] content) {
		return this.create(status.value(), headers, content);
	}

	/**
	 * Creates a new {@link OperationResponse}. If the response has any content, the given
	 * {@code headers} will be augmented to ensure that they include a
	 * {@code Content-Length} header.
	 * @param status the status of the response
	 * @param headers the response's headers
	 * @param content the content of the response
	 * @return the {@code OperationResponse}
	 */
	public OperationResponse create(int status, HttpHeaders headers, byte[] content) {
		return new StandardOperationResponse(status, augmentHeaders(headers, content), content);
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
		return new StandardOperationResponse(original.getStatusCode(),
				getUpdatedHeaders(original.getHeaders(), newContent), newContent);
	}

	/**
	 * Creates a new {@code OperationResponse} based on the given {@code original} but
	 * with the given {@code newHeaders}.
	 * @param original the original response
	 * @param newHeaders the new headers
	 * @return the new response with the new headers
	 */
	public OperationResponse createFrom(OperationResponse original, HttpHeaders newHeaders) {
		return new StandardOperationResponse(original.getStatusCode(), newHeaders, original.getContent());
	}

	private HttpHeaders augmentHeaders(HttpHeaders originalHeaders, byte[] content) {
		return new HttpHeadersHelper(originalHeaders).setContentLengthHeader(content).getHeaders();
	}

	private HttpHeaders getUpdatedHeaders(HttpHeaders originalHeaders, byte[] updatedContent) {
		return new HttpHeadersHelper(originalHeaders).updateContentLengthHeaderIfPresent(updatedContent).getHeaders();
	}

}
