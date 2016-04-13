/*
 * Copyright 2014-2016 the original author or authors.
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

import java.net.URI;
import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * A factory for creating {@link OperationRequest OperationRequests}.
 *
 * @author Andy Wilkinson
 */
public class OperationRequestFactory {

	/**
	 * Creates a new {@link OperationRequest}. The given {@code headers} will be augmented
	 * to ensure that they always include a {@code Content-Length} header if the request
	 * has any content and a {@code Host} header.
	 *
	 * @param uri the request's uri
	 * @param method the request method
	 * @param content the content of the request
	 * @param headers the request's headers
	 * @param parameters the request's parameters
	 * @param parts the request's parts
	 * @return the {@code OperationRequest}
	 */
	public OperationRequest create(URI uri, HttpMethod method, byte[] content,
			HttpHeaders headers, Parameters parameters,
			Collection<OperationRequestPart> parts) {
		return new StandardOperationRequest(uri, method, content,
				augmentHeaders(headers, uri, content), parameters, parts);
	}

	/**
	 * Creates a new {@code OperationRequest} based on the given {@code original} but with
	 * the given {@code newContent}. If the original request had a {@code Content-Length}
	 * header it will be modified to match the length of the new content.
	 *
	 * @param original The original request
	 * @param newContent The new content
	 *
	 * @return The new request with the new content
	 */
	public OperationRequest createFrom(OperationRequest original, byte[] newContent) {
		return new StandardOperationRequest(original.getUri(), original.getMethod(),
				newContent, getUpdatedHeaders(original.getHeaders(), newContent),
				original.getParameters(), original.getParts());
	}

	/**
	 * Creates a new {@code OperationRequest} based on the given {@code original} but with
	 * the given {@code newHeaders}.
	 *
	 * @param original The original request
	 * @param newHeaders The new headers
	 *
	 * @return The new request with the new headers
	 */
	public OperationRequest createFrom(OperationRequest original,
			HttpHeaders newHeaders) {
		return new StandardOperationRequest(original.getUri(), original.getMethod(),
				original.getContent(), newHeaders, original.getParameters(),
				original.getParts());
	}

	/**
	 * Creates a new {@code OperationRequest} based on the given {@code original} but with
	 * the given {@code newParameters}.
	 *
	 * @param original The original request
	 * @param newParameters The new parameters
	 *
	 * @return The new request with the new parameters
	 */
	public OperationRequest createFrom(OperationRequest original,
			Parameters newParameters) {
		return new StandardOperationRequest(original.getUri(), original.getMethod(),
				original.getContent(), original.getHeaders(), newParameters,
				original.getParts());
	}

	private HttpHeaders augmentHeaders(HttpHeaders originalHeaders, URI uri,
			byte[] content) {
		return new HttpHeadersHelper(originalHeaders)
				.addIfAbsent(HttpHeaders.HOST, uri.getHost())
				.setContentLengthHeader(content).getHeaders();
	}

	private HttpHeaders getUpdatedHeaders(HttpHeaders originalHeaders,
			byte[] updatedContent) {
		return new HttpHeadersHelper(originalHeaders)
				.updateContentLengthHeaderIfPresent(updatedContent).getHeaders();
	}

}
