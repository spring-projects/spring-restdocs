/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.operation.preprocess;

import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.Parameters;

/**
 * An {@link OperationPreprocessor} that can be used to modify a request's
 * {@link OperationRequest#getParameters()} by adding, setting, and removing parameters.
 *
 * @author Andy Wilkinson
 * @author Jihun Cha
 * @since 1.1.0
 */
public final class ParametersModifyingOperationPreprocessor extends
		MultiValueMapModifyingOperationPreprocessorAdapter<ParametersModifyingOperationPreprocessor, Parameters> {

	private final OperationRequestFactory requestFactory = new OperationRequestFactory();

	@Override
	public OperationRequest preprocess(OperationRequest request) {
		Parameters parameters = new Parameters();
		parameters.putAll(request.getParameters());
		for (Modification<Parameters> modification : getModifications()) {
			modification.apply(parameters);
		}
		return this.requestFactory.createFrom(request, parameters);
	}

}
