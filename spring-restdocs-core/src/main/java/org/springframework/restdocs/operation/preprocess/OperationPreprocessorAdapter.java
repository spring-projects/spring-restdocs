/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationResponse;

/**
 * An implementation of {@link OperationPreprocessor} that returns the request and
 * response as-is. To be subclassed by preprocessor implementations that only modify the
 * request or the response.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public abstract class OperationPreprocessorAdapter implements OperationPreprocessor {

	/**
	 * Returns the given {@code request} as-is.
	 * @param request the request
	 * @return the unmodified request
	 */
	@Override
	public OperationRequest preprocess(OperationRequest request) {
		return request;
	}

	/**
	 * Returns the given {@code response} as-is.
	 * @param response the response
	 * @return the unmodified response
	 */
	@Override
	public OperationResponse preprocess(OperationResponse response) {
		return response;
	}

}
