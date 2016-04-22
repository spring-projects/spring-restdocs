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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.SnippetException;

/**
 * A {@link Snippet} that documents the request parameters supported by a RESTful
 * resource.
 * <p>
 * Request parameters are sent as part of the query string or as POSTed form data.
 *
 * @author Andy Wilkinson
 * @see OperationRequest#getParameters()
 * @see RequestDocumentation#requestParameters(ParameterDescriptor...)
 * @see RequestDocumentation#requestParameters(Map, ParameterDescriptor...)
 */
public class RequestParametersSnippet extends AbstractParametersSnippet {

	/**
	 * Creates a new {@code RequestParametersSnippet} that will document the request's
	 * parameters using the given {@code descriptors}. Undocumented parameters will
	 * trigger a failure.
	 *
	 * @param descriptors the parameter descriptors
	 */
	protected RequestParametersSnippet(List<ParameterDescriptor> descriptors) {
		this(descriptors, null, false);
	}

	/**
	 * Creates a new {@code RequestParametersSnippet} that will document the request's
	 * parameters using the given {@code descriptors}. If
	 * {@code ignoreUndocumentedParameters} is {@code true}, undocumented parameters will
	 * be ignored and will not trigger a failure.
	 *
	 * @param descriptors the parameter descriptors
	 * @param ignoreUndocumentedParameters whether undocumented parameters should be
	 * ignored
	 */
	protected RequestParametersSnippet(List<ParameterDescriptor> descriptors,
			boolean ignoreUndocumentedParameters) {
		this(descriptors, null, ignoreUndocumentedParameters);
	}

	/**
	 * Creates a new {@code RequestParametersSnippet} that will document the request's
	 * parameters using the given {@code descriptors}. The given {@code attributes} will
	 * be included in the model during template rendering. Undocumented parameters will
	 * trigger a failure.
	 *
	 * @param descriptors the parameter descriptors
	 * @param attributes the additional attributes
	 */
	protected RequestParametersSnippet(List<ParameterDescriptor> descriptors,
			Map<String, Object> attributes) {
		this(descriptors, attributes, false);
	}

	/**
	 * Creates a new {@code RequestParametersSnippet} that will document the request's
	 * parameters using the given {@code descriptors}. The given {@code attributes} will
	 * be included in the model during template rendering. If
	 * {@code ignoreUndocumentedParameters} is {@code true}, undocumented parameters will
	 * be ignored and will not trigger a failure.
	 *
	 * @param descriptors the parameter descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedParameters whether undocumented parameters should be
	 * ignored
	 */
	protected RequestParametersSnippet(List<ParameterDescriptor> descriptors,
			Map<String, Object> attributes, boolean ignoreUndocumentedParameters) {
		super("request-parameters", descriptors, attributes,
				ignoreUndocumentedParameters);
	}

	@Override
	protected void verificationFailed(Set<String> undocumentedParameters,
			Set<String> missingParameters) {
		String message = "";
		if (!undocumentedParameters.isEmpty()) {
			message += "Request parameters with the following names were not documented: "
					+ undocumentedParameters;
		}
		if (!missingParameters.isEmpty()) {
			if (message.length() > 0) {
				message += ". ";
			}
			message += "Request parameters with the following names were not found in the request: "
					+ missingParameters;
		}
		throw new SnippetException(message);
	}

	@Override
	protected Set<String> extractActualParameters(Operation operation) {
		return operation.getRequest().getParameters().keySet();
	}

	/**
	 * Returns a new {@code RequestParametersSnippet} configured with this snippet's
	 * attributes and its descriptors combined with the given
	 * {@code additionalDescriptors}.
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public RequestParametersSnippet and(ParameterDescriptor... additionalDescriptors) {
		List<ParameterDescriptor> combinedDescriptors = new ArrayList<>();
		combinedDescriptors.addAll(getParameterDescriptors().values());
		combinedDescriptors.addAll(Arrays.asList(additionalDescriptors));
		return new RequestParametersSnippet(combinedDescriptors, this.getAttributes());
	}

}
