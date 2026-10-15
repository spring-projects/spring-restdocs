/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationResponse;

/**
 * An {@code OperationPreprocessor} processes the {@link OperationRequest} and
 * {@link OperationResponse} of an {@link Operation} prior to it being documented.
 *
 * @author Andy Wilkinson
 */
public interface OperationPreprocessor {

	/**
	 * Processes the given {@code request}.
	 * @param request the request to process
	 * @return the processed request
	 */
	OperationRequest preprocess(OperationRequest request);

	/**
	 * Processes the given {@code response}.
	 * @param response the response to process
	 * @return the processed response
	 */
	OperationResponse preprocess(OperationResponse response);

}
