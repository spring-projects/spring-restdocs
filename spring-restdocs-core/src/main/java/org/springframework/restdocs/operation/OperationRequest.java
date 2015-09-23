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

import java.net.URI;
import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * The request that was sent as part of performing an operation on a RESTful service.
 *
 * @author Andy Wilkinson
 * @see Operation#getRequest()
 */
public interface OperationRequest {

	/**
	 * Returns the content of the request. If the request has no content an empty array is
	 * returned.
	 *
	 * @return the contents, never {@code null}
	 */
	byte[] getContent();

	/**
	 * Returns the content of the request as a {@link String}. If the request has no
	 * content an empty string is returned. If the request has a {@code Content-Type}
	 * header that specifies a charset then that charset will be used when converting the
	 * contents to a {@code String}.
	 *
	 * @return the contents as string, never {@code null}
	 */
	String getContentAsString();

	/**
	 * Returns the headers that were included in the request.
	 *
	 * @return the headers
	 */
	HttpHeaders getHeaders();

	/**
	 * Returns the HTTP method of the request.
	 *
	 * @return the HTTP method
	 */
	HttpMethod getMethod();

	/**
	 * Returns the request's parameters. For a {@code GET} request, the parameters are
	 * derived from the query string. For a {@code POST} request, the parameters are
	 * derived form the request's body.
	 *
	 * @return the parameters
	 */
	Parameters getParameters();

	/**
	 * Returns the request's parts, provided that it is a multipart request. If not, then
	 * an empty {@link Collection} is returned.
	 *
	 * @return the parts
	 */
	Collection<OperationRequestPart> getParts();

	/**
	 * Returns the request's URI.
	 *
	 * @return the URI
	 */
	URI getUri();

}
