/*
 * Copyright 2014-present the original author or authors.
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
	 * @param name the name of the operation
	 * @param request the request that was sent
	 * @param response the response that was received
	 * @param attributes attributes to associate with the operation
	 */
	public StandardOperation(String name, OperationRequest request, OperationResponse response,
			Map<String, Object> attributes) {
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
