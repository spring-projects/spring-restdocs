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

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * The response that was received as part of performing an operation on a RESTful service.
 *
 * @author Andy Wilkinson
 * @see Operation
 * @see Operation#getRequest()
 */
public interface OperationResponse {

	/**
	 * Returns the status of the response.
	 *
	 * @return the status
	 */
	HttpStatus getStatus();

	/**
	 * Returns the headers in the response.
	 *
	 * @return the headers
	 */
	HttpHeaders getHeaders();

	/**
	 * Returns the contents of the response. If the response has no content an empty array
	 * is returned.
	 *
	 * @return the contents, never {@code null}
	 */
	byte[] getContent();

	/**
	 * Returns the contents as string of the response. If the response has no content an empty string
	 * is returned
	 * @return the contents as string, never {@code null}
	 * @throws IOException if an input or output exception occurred
	 */
	String getContentAsString() throws IOException;
}
