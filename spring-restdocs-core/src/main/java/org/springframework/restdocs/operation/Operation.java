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

import java.util.Map;

/**
 * Describes an operation performed on a RESTful service.
 *
 * @author Andy Wilkinson
 */
public interface Operation {

	/**
	 * Returns a {@code Map} of attributes associated with the operation.
	 *
	 * @return the attributes
	 */
	Map<String, Object> getAttributes();

	/**
	 * Returns the name of the operation.
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Returns the request that was sent.
	 *
	 * @return the request
	 */
	OperationRequest getRequest();

	/**
	 * Returns the response that was received.
	 *
	 * @return the response
	 */
	OperationResponse getResponse();

}
