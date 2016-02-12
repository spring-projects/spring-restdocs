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

/**
 * Standard implementation of {@link OperationResponse}.
 *
 * @author Andy Wilkinson
 */
class StandardOperationResponse extends AbstractOperationMessage
		implements OperationResponse {

	private final HttpStatus status;

	/**
	 * Creates a new response with the given {@code status}, {@code headers}, and
	 * {@code content}.
	 *
	 * @param status the status of the response
	 * @param headers the headers of the response
	 * @param content the content of the response
	 */
	StandardOperationResponse(HttpStatus status, HttpHeaders headers, byte[] content) {
		super(content, headers);
		this.status = status;
	}

	@Override
	public HttpStatus getStatus() {
		return this.status;
	}

}
