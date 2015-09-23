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
	 *
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
