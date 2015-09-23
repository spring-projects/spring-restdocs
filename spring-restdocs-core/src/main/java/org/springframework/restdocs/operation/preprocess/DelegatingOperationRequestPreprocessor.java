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
	 *
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
