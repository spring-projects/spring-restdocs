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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A {@link Snippet} that documents the request parameters supported by a RESTful
 * resource.
 * <p>
 * Request parameters are sent as part of the query string or as posted from data.
 *
 * @author Andy Wilkinson
 * @see ServletRequest#getParameterMap()
 * @see RequestParam
 */
class RequestParametersSnippet extends AbstractParametersSnippet {

	RequestParametersSnippet(List<ParameterDescriptor> descriptors) {
		this(null, descriptors);
	}

	RequestParametersSnippet(Map<String, Object> attributes,
			List<ParameterDescriptor> descriptors) {
		super("request-parameters", attributes, descriptors);
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

}
