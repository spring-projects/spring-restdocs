/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import org.springframework.restdocs.operation.OperationRequest;

/**
 * An {@code OperationRequestPreprocessor} is used to modify an {@code OperationRequest}
 * prior to it being documented.
 *
 * @author Andy Wilkinson
 */
public interface OperationRequestPreprocessor {

	/**
	 * Processes and potentially modifies the given {@code request} before it is
	 * documented.
	 * @param request the request
	 * @return the modified request
	 */
	OperationRequest preprocess(OperationRequest request);

}
