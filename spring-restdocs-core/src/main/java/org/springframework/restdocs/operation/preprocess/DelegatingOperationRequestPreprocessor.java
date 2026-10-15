/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import java.util.List;

import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.util.Assert;

/**
 * An {@link OperationRequestPreprocessor} that delgates to one or more
 * {@link OperationPreprocessor OperationPreprocessors} to preprocess an
 * {@link OperationRequest}.
 *
 * @author Andy Wilkinson
 *
 */
class DelegatingOperationRequestPreprocessor implements OperationRequestPreprocessor {

	private final List<OperationPreprocessor> delegates;

	/**
	 * Creates a new {@code DelegatingOperationRequestPreprocessor} that will delegate to
	 * the given {@code delegates} by calling
	 * {@link OperationPreprocessor#preprocess(OperationRequest)}.
	 * @param delegates the delegates
	 */
	DelegatingOperationRequestPreprocessor(List<OperationPreprocessor> delegates) {
		Assert.notNull(delegates, "delegates must be non-null");
		this.delegates = delegates;
	}

	@Override
	public OperationRequest preprocess(OperationRequest operationRequest) {
		OperationRequest preprocessedRequest = operationRequest;
		for (OperationPreprocessor delegate : this.delegates) {
			preprocessedRequest = delegate.preprocess(preprocessedRequest);
		}
		return preprocessedRequest;
	}

}
