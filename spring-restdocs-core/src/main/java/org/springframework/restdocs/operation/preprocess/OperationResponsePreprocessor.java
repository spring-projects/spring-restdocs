/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import org.springframework.restdocs.operation.OperationResponse;

/**
 * An {@code OperationResponsePreprocessor} is used to modify an {@code OperationResponse}
 * prior to it being documented.
 *
 * @author Andy Wilkinson
 */
public interface OperationResponsePreprocessor {

	/**
	 * Processes and potentially modifies the given {@code response} before it is
	 * documented.
	 * @param response the response
	 * @return the modified response
	 */
	OperationResponse preprocess(OperationResponse response);

}
