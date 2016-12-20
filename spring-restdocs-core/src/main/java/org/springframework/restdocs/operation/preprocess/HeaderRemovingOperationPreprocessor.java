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

package org.springframework.restdocs.operation.preprocess;

import java.util.Iterator;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;

/**
 * An {@link OperationPreprocessor} that removes headers. The headers to remove are
 * provided as constructor arguments and can be either plain string or patterns to match
 * against the headers found
 *
 * @author Andy Wilkinson
 */
class HeaderRemovingOperationPreprocessor implements OperationPreprocessor {

	private final OperationRequestFactory requestFactory = new OperationRequestFactory();

	private final OperationResponseFactory responseFactory = new OperationResponseFactory();

	private final HeaderFilter headerFilter;

	HeaderRemovingOperationPreprocessor(HeaderFilter headerFilter) {
		this.headerFilter = headerFilter;
	}

	@Override
	public OperationResponse preprocess(OperationResponse response) {
		return this.responseFactory.createFrom(response,
				removeHeaders(response.getHeaders()));
	}

	@Override
	public OperationRequest preprocess(OperationRequest request) {
		return this.requestFactory.createFrom(request,
				removeHeaders(request.getHeaders()));
	}

	private HttpHeaders removeHeaders(HttpHeaders originalHeaders) {
		HttpHeaders processedHeaders = new HttpHeaders();
		processedHeaders.putAll(originalHeaders);
		Iterator<String> headers = processedHeaders.keySet().iterator();
		while (headers.hasNext()) {
			if (this.headerFilter.excludeHeader(headers.next())) {
				headers.remove();
			}
		}
		return processedHeaders;
	}
}
