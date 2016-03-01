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

package org.springframework.restdocs.cli;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.Base64Utils;

/**
 * An abstract {@link Snippet} that for CLI requests.
 *
 * @author Andy Wilkinson
 * @author Paul-Christian Volkmer
 * @author Raman Gupta
 */
public abstract class AbstractCliSnippet extends TemplatedSnippet {

	private static final Set<HeaderFilter> HEADER_FILTERS;

	static {
		Set<HeaderFilter> headerFilters = new HashSet<>();
		headerFilters.add(new NamedHeaderFilter(HttpHeaders.HOST));
		headerFilters.add(new NamedHeaderFilter(HttpHeaders.CONTENT_LENGTH));
		headerFilters.add(new BasicAuthHeaderFilter());
		HEADER_FILTERS = Collections.unmodifiableSet(headerFilters);
	}

	/**
	 * Create a new abstract cli snippet with the given name and attributes.
	 * @param snippetName The snippet name.
	 * @param attributes The snippet attributes.
	 */
	protected AbstractCliSnippet(String snippetName, Map<String, Object> attributes) {
		super(snippetName, attributes);
	}

	/**
	 * Create the model which will be passed to the template for rendering.
	 * @param operation The operation
	 * @return The model.
	 */
	protected abstract Map<String, Object> createModel(Operation operation);

	/**
	 * Gets the unique parameters given a request.
	 * @param request The operation request.
	 * @return The unique parameters.
	 */
	protected Parameters getUniqueParameters(OperationRequest request) {
		Parameters queryStringParameters = new QueryStringParser()
				.parse(request.getUri());
		Parameters uniqueParameters = new Parameters();

		for (Map.Entry<String, List<String>> parameter : request.getParameters().entrySet()) {
			addIfUnique(parameter, queryStringParameters, uniqueParameters);
		}
		return uniqueParameters;
	}

	private void addIfUnique(Map.Entry<String, List<String>> parameter,
			Parameters queryStringParameters, Parameters uniqueParameters) {
		if (!queryStringParameters.containsKey(parameter.getKey())) {
			uniqueParameters.put(parameter.getKey(), parameter.getValue());
		}
		else {
			List<String> candidates = parameter.getValue();
			List<String> existing = queryStringParameters.get(parameter.getKey());
			for (String candidate : candidates) {
				if (!existing.contains(candidate)) {
					uniqueParameters.add(parameter.getKey(), candidate);
				}
			}
		}
	}

	/**
	 * Whether the request operation is a PUT or a POST.
	 * @param request The request.
	 * @return boolean
	 */
	protected boolean isPutOrPost(OperationRequest request) {
		return HttpMethod.PUT.equals(request.getMethod())
				|| HttpMethod.POST.equals(request.getMethod());
	}

	/**
	 * Whether the passed header is allowed according to the configured
	 * header filters.
	 * @param header The header to test.
	 * @return boolean
	 */
	protected boolean allowedHeader(Map.Entry<String, List<String>> header) {
		for (HeaderFilter headerFilter : HEADER_FILTERS) {
			if (!headerFilter.allow(header.getKey(), header.getValue())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determine if the header passed is a basic auth header.
	 * @param headerValue The header to test.
	 * @return boolean
	 */
	protected boolean isBasicAuthHeader(List<String> headerValue) {
		return BasicAuthHeaderFilter.isBasicAuthHeader(headerValue);
	}

	/**
	 * Decodes a basic auth header into name:password credentials.
	 * @param headerValue The encoded header value.
	 * @return name:password credentials.
	 */
	protected String decodeBasicAuthHeader(List<String> headerValue) {
		return BasicAuthHeaderFilter.decodeBasicAuthHeader(headerValue);
	}

	private interface HeaderFilter {

		boolean allow(String name, List<String> value);
	}

	private static final class BasicAuthHeaderFilter implements HeaderFilter {

		@Override
		public boolean allow(String name, List<String> value) {
			return !(HttpHeaders.AUTHORIZATION.equals(name) && isBasicAuthHeader(value));
		}

		static boolean isBasicAuthHeader(List<String> value) {
			return value != null && (!value.isEmpty())
					&& value.get(0).startsWith("Basic ");
		}

		static String decodeBasicAuthHeader(List<String> value) {
			return new String(Base64Utils.decodeFromString(value.get(0).substring(6)));
		}

	}

	private static final class NamedHeaderFilter implements HeaderFilter {

		private final String name;

		NamedHeaderFilter(String name) {
			this.name = name;
		}

		@Override
		public boolean allow(String name, List<String> value) {
			return !this.name.equalsIgnoreCase(name);
		}

	}

}
