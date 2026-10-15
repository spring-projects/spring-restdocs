/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import java.util.List;

import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.util.Assert;

/**
 * An {@link OperationResponsePreprocessor} that delgates to one or more
 * {@link OperationPreprocessor OperationPreprocessors} to preprocess an
 * {@link OperationResponse}.
 *
 * @author Andy Wilkinson
 */
class DelegatingOperationResponsePreprocessor implements OperationResponsePreprocessor {

	private final List<OperationPreprocessor> delegates;

	/**
	 * Creates a new {@code DelegatingOperationResponsePreprocessor} that will delegate to
	 * the given {@code delegates} by calling
	 * {@link OperationPreprocessor#preprocess(OperationResponse)}.
	 * @param delegates the delegates
	 */
	DelegatingOperationResponsePreprocessor(List<OperationPreprocessor> delegates) {
		Assert.notNull(delegates, "delegates must be non-null");
		this.delegates = delegates;
	}

	@Override
	public OperationResponse preprocess(OperationResponse response) {
		OperationResponse preprocessedResponse = response;
		for (OperationPreprocessor delegate : this.delegates) {
			preprocessedResponse = delegate.preprocess(preprocessedResponse);
		}
		return preprocessedResponse;
	}

}
