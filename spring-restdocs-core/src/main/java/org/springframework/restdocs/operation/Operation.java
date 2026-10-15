/*
 * Copyright 2014-present the original author or authors.
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
	 * @return the attributes
	 */
	Map<String, Object> getAttributes();

	/**
	 * Returns the name of the operation.
	 * @return the name
	 */
	String getName();

	/**
	 * Returns the request that was sent.
	 * @return the request
	 */
	OperationRequest getRequest();

	/**
	 * Returns the response that was received.
	 * @return the response
	 */
	OperationResponse getResponse();

}
