/*
 * Copyright 2014-2019 the original author or authors.
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

package org.springframework.restdocs.operation.preprocess;

import java.util.LinkedList;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;

/**
 * An {@link OperationPreprocessor} that can be used to modify a request's
 * {@link OperationRequest#getHeaders()} or response's
 * {@link OperationResponse#getHeaders()} by adding, setting, and removing headers.
 *
 * @author Andy Wilkinson
 * @author Jihun Cha
 */
public class HeadersModifyingOperationPreprocessor extends
		MultiValueMapModifyingOperationPreprocessorAdapter<HeadersModifyingOperationPreprocessor, HttpHeaders> {

	private final OperationRequestFactory requestFactory = new OperationRequestFactory();

	private final OperationResponseFactory responseFactory = new OperationResponseFactory();

	@Override
	public OperationRequest preprocess(OperationRequest request) {
		HttpHeaders headers = writableHttpHeaders(request.getHeaders());
		for (Modification<HttpHeaders> modification : getModifications()) {
			modification.apply(headers);
		}
		return this.requestFactory.createFrom(request, headers);
	}

	@Override
	public OperationResponse preprocess(OperationResponse response) {
		HttpHeaders headers = writableHttpHeaders(response.getHeaders());
		for (Modification<HttpHeaders> modification : getModifications()) {
			modification.apply(headers);
		}
		return this.responseFactory.createFrom(response, headers);
	}

	private HttpHeaders writableHttpHeaders(HttpHeaders headers) {
		HttpHeaders writable = new HttpHeaders();
		headers.keySet()
				.forEach((key) -> writable.put(key, new LinkedList<>(headers.get(key))));
		return writable;
	}

}
