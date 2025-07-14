/*
 * Copyright 2014-present the original author or authors.
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

package org.springframework.restdocs.operation;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.springframework.util.LinkedMultiValueMap;

/**
 * A request's query parameters, derived from its URI's query string.
 *
 * @author Andy Wilkinson
 * @since 3.0.0
 */
public final class QueryParameters extends LinkedMultiValueMap<String, String> {

	private QueryParameters() {

	}

	/**
	 * Extracts the query parameters from the query string of the given {@code request}.
	 * If the request has no query string, an empty {@code QueryParameters} is returned,
	 * rather than {@code null}.
	 * @param request the request
	 * @return the query parameters extracted from the request's query string
	 */
	public static QueryParameters from(OperationRequest request) {
		return from(request.getUri().getRawQuery());
	}

	private static QueryParameters from(String queryString) {
		if (queryString == null || queryString.length() == 0) {
			return new QueryParameters();
		}
		return parse(queryString);
	}

	private static QueryParameters parse(String query) {
		QueryParameters parameters = new QueryParameters();
		try (Scanner scanner = new Scanner(query)) {
			scanner.useDelimiter("&");
			while (scanner.hasNext()) {
				processParameter(scanner.next(), parameters);
			}
		}
		return parameters;
	}

	private static void processParameter(String parameter, QueryParameters parameters) {
		String[] components = parameter.split("=");
		if (components.length > 0 && components.length < 3) {
			if (components.length == 2) {
				String name = components[0];
				String value = components[1];
				parameters.add(decode(name), decode(value));
			}
			else {
				List<String> values = parameters.computeIfAbsent(components[0], (p) -> new LinkedList<>());
				values.add("");
			}
		}
		else {
			throw new IllegalArgumentException("The parameter '" + parameter + "' is malformed");
		}
	}

	private static String decode(String encoded) {
		return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
	}

}
