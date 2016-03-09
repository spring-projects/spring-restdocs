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

package org.springframework.restdocs.cli;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.util.Base64Utils;

/**
 * An {@link OperationRequest} wrapper with methods that are useful when producing a
 * snippet containing a CLI command for a request.
 *
 * @author Andy Wilkinson
 * @author Raman Gupta
 */
final class CliOperationRequest implements OperationRequest {

	private static final Set<HeaderFilter> HEADER_FILTERS;

	static {
		Set<HeaderFilter> headerFilters = new HashSet<>();
		headerFilters.add(new NamedHeaderFilter(HttpHeaders.HOST));
		headerFilters.add(new NamedHeaderFilter(HttpHeaders.CONTENT_LENGTH));
		headerFilters.add(new BasicAuthHeaderFilter());
		HEADER_FILTERS = Collections.unmodifiableSet(headerFilters);
	}

	private final OperationRequest delegate;

	CliOperationRequest(OperationRequest delegate) {
		this.delegate = delegate;
	}

	Parameters getUniqueParameters() {
		Parameters queryStringParameters = new QueryStringParser()
				.parse(this.delegate.getUri());
		Parameters uniqueParameters = new Parameters();

		for (Map.Entry<String, List<String>> parameter : this.delegate.getParameters()
				.entrySet()) {
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

	boolean isPutOrPost() {
		return HttpMethod.PUT.equals(this.delegate.getMethod())
				|| HttpMethod.POST.equals(this.delegate.getMethod());
	}

	String getBasicAuthCredentials() {
		List<String> headerValue = this.delegate.getHeaders()
				.get(HttpHeaders.AUTHORIZATION);
		if (BasicAuthHeaderFilter.isBasicAuthHeader(headerValue)) {
			return BasicAuthHeaderFilter.decodeBasicAuthHeader(headerValue);
		}
		return null;
	}

	@Override
	public byte[] getContent() {
		return this.delegate.getContent();
	}

	@Override
	public String getContentAsString() {
		return this.delegate.getContentAsString();
	}

	@Override
	public HttpHeaders getHeaders() {
		HttpHeaders filteredHeaders = new HttpHeaders();
		for (Entry<String, List<String>> header : this.delegate.getHeaders().entrySet()) {
			if (allowedHeader(header)) {
				filteredHeaders.put(header.getKey(), header.getValue());
			}
		}
		return HttpHeaders.readOnlyHttpHeaders(filteredHeaders);
	}

	private boolean allowedHeader(Map.Entry<String, List<String>> header) {
		for (HeaderFilter headerFilter : HEADER_FILTERS) {
			if (!headerFilter.allow(header.getKey(), header.getValue())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public HttpMethod getMethod() {
		return this.delegate.getMethod();
	}

	@Override
	public Parameters getParameters() {
		return this.delegate.getParameters();
	}

	@Override
	public Collection<OperationRequestPart> getParts() {
		return this.delegate.getParts();
	}

	@Override
	public URI getUri() {
		return this.delegate.getUri();
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
