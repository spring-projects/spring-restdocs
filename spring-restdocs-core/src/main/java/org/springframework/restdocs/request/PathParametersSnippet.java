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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.SnippetException;
import org.springframework.util.Assert;

/**
 * A {@link Snippet} that documents the path parameters supported by a RESTful resource.
 *
 * @author Andy Wilkinson
 * @see RequestDocumentation#pathParameters(ParameterDescriptor...)
 * @see RequestDocumentation#pathParameters(Map, ParameterDescriptor...)
 */
public class PathParametersSnippet extends AbstractParametersSnippet {

	private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

	/**
	 * Creates a new {@code PathParametersSnippet} that will document the request's path
	 * parameters using the given {@code descriptors}.
	 * 
	 * @param descriptors the parameter descriptors
	 */
	protected PathParametersSnippet(List<ParameterDescriptor> descriptors) {
		this(descriptors, null);
	}

	/**
	 * Creates a new {@code PathParametersSnippet} that will document the request's path
	 * parameters using the given {@code descriptors}. The given {@code attributes} will
	 * be included in the model during template rendering.
	 * 
	 * @param descriptors the parameter descriptors
	 * @param attributes the additional attributes
	 */
	protected PathParametersSnippet(List<ParameterDescriptor> descriptors,
			Map<String, Object> attributes) {
		super("path-parameters", descriptors, attributes);
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		Map<String, Object> model = super.createModel(operation);
		model.put("path", removeQueryStringIfPresent(extractUrlTemplate(operation)));
		return model;
	}

	private String removeQueryStringIfPresent(String urlTemplate) {
		int index = urlTemplate.indexOf('?');
		if (index == -1) {
			return urlTemplate;
		}
		return urlTemplate.substring(0, index);
	}

	@Override
	protected Set<String> extractActualParameters(Operation operation) {
		String urlTemplate = extractUrlTemplate(operation);
		Matcher matcher = NAMES_PATTERN.matcher(urlTemplate);
		Set<String> actualParameters = new HashSet<>();
		while (matcher.find()) {
			String match = matcher.group(1);
			actualParameters.add(getParameterName(match));
		}
		return actualParameters;
	}

	private String extractUrlTemplate(Operation operation) {
		String urlTemplate = (String) operation.getAttributes().get(
				"org.springframework.restdocs.urlTemplate");
		Assert.notNull(urlTemplate,
				"urlTemplate not found. Did you use RestDocumentationRequestBuilders to "
						+ "build the request?");
		return urlTemplate;
	}

	private static String getParameterName(String match) {
		int colonIndex = match.indexOf(':');
		return colonIndex != -1 ? match.substring(0, colonIndex) : match;
	}

	@Override
	protected void verificationFailed(Set<String> undocumentedParameters,
			Set<String> missingParameters) {
		String message = "";
		if (!undocumentedParameters.isEmpty()) {
			message += "Path parameters with the following names were not documented: "
					+ undocumentedParameters;
		}
		if (!missingParameters.isEmpty()) {
			if (message.length() > 0) {
				message += ". ";
			}
			message += "Path parameters with the following names were not found in "
					+ "the request: " + missingParameters;
		}
		throw new SnippetException(message);
	}

}
