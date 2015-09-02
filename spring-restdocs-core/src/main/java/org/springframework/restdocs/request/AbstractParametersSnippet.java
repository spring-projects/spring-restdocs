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

package org.springframework.restdocs.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.Assert;

abstract class AbstractParametersSnippet extends TemplatedSnippet {

	private final Map<String, ParameterDescriptor> descriptorsByName = new LinkedHashMap<>();

	protected AbstractParametersSnippet(String snippetName,
			Map<String, Object> attributes, List<ParameterDescriptor> descriptors) {
		super(snippetName, attributes);
		for (ParameterDescriptor descriptor : descriptors) {
			Assert.hasText(descriptor.getName());
			Assert.hasText(descriptor.getDescription());
			this.descriptorsByName.put(descriptor.getName(), descriptor);
		}
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) throws IOException {
		verifyParameterDescriptors(operation);

		Map<String, Object> model = new HashMap<>();
		List<Map<String, Object>> parameters = new ArrayList<>();
		for (Entry<String, ParameterDescriptor> entry : this.descriptorsByName.entrySet()) {
			parameters.add(entry.getValue().toModel());
		}
		model.put("parameters", parameters);
		return model;
	}

	protected void verifyParameterDescriptors(Operation operation) {
		Set<String> actualParameters = extractActualParameters(operation);
		Set<String> expectedParameters = this.descriptorsByName.keySet();
		Set<String> undocumentedParameters = new HashSet<String>(actualParameters);
		undocumentedParameters.removeAll(expectedParameters);
		Set<String> missingParameters = new HashSet<String>(expectedParameters);
		missingParameters.removeAll(actualParameters);

		if (!undocumentedParameters.isEmpty() || !missingParameters.isEmpty()) {
			verificationFailed(undocumentedParameters, missingParameters);
		}
		else {
			Assert.isTrue(actualParameters.equals(expectedParameters));
		}
	}

	protected abstract Set<String> extractActualParameters(Operation operation);

	protected abstract void verificationFailed(Set<String> undocumentedParameters,
			Set<String> missingParameters);

}
