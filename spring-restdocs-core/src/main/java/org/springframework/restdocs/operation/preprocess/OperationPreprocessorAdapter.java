/*
 * Copyright 2012-2016 the original author or authors.
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

import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationResponse;

/**
 * An implementation of {@link OperationPreprocessor} that returns the request and
 * response as-is. To be subclasses by preprocessor implementations that only modify the
 * request or the response.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public abstract class OperationPreprocessorAdapter implements OperationPreprocessor {

	/**
	 * Returns the given {@code request} as-is.
	 *
	 * @param request the request
	 * @return the unmodified request
	 */
	@Override
	public OperationRequest preprocess(OperationRequest request) {
		return request;
	}

	/**
	 * Returns the given {@code response} as-is.
	 *
	 * @param response the response
	 * @return the unmodified response
	 */
	@Override
	public OperationResponse preprocess(OperationResponse response) {
		return response;
	}

}
