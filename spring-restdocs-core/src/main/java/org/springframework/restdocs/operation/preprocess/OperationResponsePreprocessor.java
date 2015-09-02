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

import org.springframework.restdocs.operation.OperationResponse;

/**
 * An {@code OperationRequestPreprocessor} is used to modify an {@code OperationRequest}
 * prior to it being documented.
 *
 * @author Andy Wilkinson
 */
public interface OperationResponsePreprocessor {

	/**
	 * Processes and potentially modifies the given {@code response} before it is
	 * documented.
	 *
	 * @param response the response
	 * @return the modified response
	 */
	OperationResponse preprocess(OperationResponse response);

}
