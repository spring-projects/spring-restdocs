/*
 * Copyright 2014-2016 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collections;
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

/**
 * Abstract {@link TemplatedSnippet} subclass that provides a base for snippets that
 * document parameters from a request sent to a RESTful resource.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 */
public abstract class AbstractParametersSnippet extends TemplatedSnippet {

	private final Map<String, ParameterDescriptor> descriptorsByName = new LinkedHashMap<>();

	private final boolean ignoreUndocumentedParameters;

	/**
	 * Creates a new {@code AbstractParametersSnippet} that will produce a snippet with
	 * the given {@code snippetName} that will document parameters using the given
	 * {@code descriptors}. The given {@code attributes} will be included in the model
	 * during template rendering. Undocumented parameters will trigger a failure.
	 *
	 * @param snippetName The snippet name
	 * @param descriptors The descriptors
	 * @param attributes The additional attributes
	 * @deprecated since 1.1 in favour of
	 * {@link #AbstractParametersSnippet(String, List, Map, boolean)}
	 */
	@Deprecated
	protected AbstractParametersSnippet(String snippetName,
			List<ParameterDescriptor> descriptors, Map<String, Object> attributes) {
		this(snippetName, descriptors, attributes, false);
	}

	/**
	 * Creates a new {@code AbstractParametersSnippet} that will produce a snippet with
	 * the given {@code snippetName} that will document parameters using the given
	 * {@code descriptors}. The given {@code attributes} will be included in the model
	 * during template rendering. If {@code ignoreUndocumentedParameters} is {@code true},
	 * undocumented parameters will be ignored and will not trigger a failure.
	 *
	 * @param snippetName The snippet name
	 * @param descriptors The descriptors
	 * @param attributes The additional attributes
	 * @param ignoreUndocumentedParameters whether undocumented parameters should be
	 * ignored
	 */
	protected AbstractParametersSnippet(String snippetName,
			List<ParameterDescriptor> descriptors, Map<String, Object> attributes,
			boolean ignoreUndocumentedParameters) {
		super(snippetName, attributes);
		for (ParameterDescriptor descriptor : descriptors) {
			Assert.notNull(descriptor.getName(),
					"Parameter descriptors must have a name");
			if (!descriptor.isIgnored()) {
				Assert.notNull(descriptor.getDescription(),
						"The descriptor for parameter '" + descriptor.getName()
								+ "' must either have a description or be marked as "
								+ "ignored");
			}
			this.descriptorsByName.put(descriptor.getName(), descriptor);
		}
		this.ignoreUndocumentedParameters = ignoreUndocumentedParameters;
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		verifyParameterDescriptors(operation);

		Map<String, Object> model = new HashMap<>();
		List<Map<String, Object>> parameters = new ArrayList<>();
		for (Entry<String, ParameterDescriptor> entry : this.descriptorsByName
				.entrySet()) {
			ParameterDescriptor descriptor = entry.getValue();
			if (!descriptor.isIgnored()) {
				parameters.add(createModelForDescriptor(descriptor));
			}
		}
		model.put("parameters", parameters);
		return model;
	}

	private void verifyParameterDescriptors(Operation operation) {
		Set<String> actualParameters = extractActualParameters(operation);
		Set<String> expectedParameters = new HashSet<>();
		for (Entry<String, ParameterDescriptor> entry : this.descriptorsByName
				.entrySet()) {
			if (!entry.getValue().isOptional()) {
				expectedParameters.add(entry.getKey());
			}
		}
		Set<String> undocumentedParameters;
		if (this.ignoreUndocumentedParameters) {
			undocumentedParameters = Collections.emptySet();
		}
		else {
			undocumentedParameters = new HashSet<>(actualParameters);
			undocumentedParameters.removeAll(expectedParameters);
		}
		Set<String> missingParameters = new HashSet<>(expectedParameters);
		missingParameters.removeAll(actualParameters);

		if (!undocumentedParameters.isEmpty() || !missingParameters.isEmpty()) {
			verificationFailed(undocumentedParameters, missingParameters);
		}
	}

	/**
	 * Extracts the names of the parameters that were present in the given
	 * {@code operation}.
	 *
	 * @param operation the operation
	 * @return the parameters
	 */
	protected abstract Set<String> extractActualParameters(Operation operation);

	/**
	 * Called when the documented parameters do not match the actual parameters.
	 *
	 * @param undocumentedParameters the parameters that were found in the operation but
	 * were not documented
	 * @param missingParameters the parameters that were documented but were not found in
	 * the operation
	 */
	protected abstract void verificationFailed(Set<String> undocumentedParameters,
			Set<String> missingParameters);

	/**
	 * Returns a {@code Map} of {@link ParameterDescriptor ParameterDescriptors} that will
	 * be used to generate the documentation key by their
	 * {@link ParameterDescriptor#getName()}.
	 *
	 * @return the map of path descriptors
	 * @deprecated since 1.1.0 in favor of {@link #getParameterDescriptors()}
	 */
	@Deprecated
	protected final Map<String, ParameterDescriptor> getFieldDescriptors() {
		return this.descriptorsByName;
	}

	/**
	 * Returns a {@code Map} of {@link ParameterDescriptor ParameterDescriptors} that will
	 * be used to generate the documentation key by their
	 * {@link ParameterDescriptor#getName()}.
	 *
	 * @return the map of path descriptors
	 */
	protected final Map<String, ParameterDescriptor> getParameterDescriptors() {
		return this.descriptorsByName;
	}

	/**
	 * Returns a model for the given {@code descriptor}.
	 *
	 * @param descriptor the descriptor
	 * @return the model
	 */
	protected Map<String, Object> createModelForDescriptor(
			ParameterDescriptor descriptor) {
		Map<String, Object> model = new HashMap<>();
		model.put("name", descriptor.getName());
		model.put("description", descriptor.getDescription());
		model.putAll(descriptor.getAttributes());
		return model;
	}

}
