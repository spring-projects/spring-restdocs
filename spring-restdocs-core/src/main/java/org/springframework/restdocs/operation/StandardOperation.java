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
 * Standard implementation of {@link Operation}.
 *
 * @author Andy Wilkinson
 */
public class StandardOperation implements Operation {

	private final String name;

	private final OperationRequest request;

	private final OperationResponse response;

	private final Map<String, Object> attributes;

	/**
	 * Creates a new {@code StandardOperation}.
	 *
	 * @param name the name of the operation
	 * @param request the request that was sent
	 * @param response the response that was received
	 * @param attributes attributes to associate with the operation
	 */
	public StandardOperation(String name, OperationRequest request,
			OperationResponse response, Map<String, Object> attributes) {
		this.name = name;
		this.request = request;
		this.response = response;
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public OperationRequest getRequest() {
		return this.request;
	}

	@Override
	public OperationResponse getResponse() {
		return this.response;
	}

}
