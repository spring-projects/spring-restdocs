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
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * A factory for creating an {@link OperationResponse} derived from a
 * {@link MockHttpServletResponse}.
 * 
 * @author Andy Wilkinson
 */
public class MockMvcOperationResponseFactory {

	/**
	 * Create a new {@code OperationResponse} derived from the given {@code mockResponse}.
	 * 
	 * @param mockResponse the response
	 * @return the {@code OperationResponse}
	 */
	public OperationResponse createOperationResponse(MockHttpServletResponse mockResponse) {
		return new StandardOperationResponse(
				HttpStatus.valueOf(mockResponse.getStatus()),
				extractHeaders(mockResponse), mockResponse.getContentAsByteArray());
	}

	private HttpHeaders extractHeaders(MockHttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		for (String headerName : response.getHeaderNames()) {
			for (String value : response.getHeaders(headerName)) {
				headers.add(headerName, value);
			}
		}
		return headers;
	}
}
