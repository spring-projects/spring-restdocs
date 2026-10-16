/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.springframework.util.LinkedMultiValueMap;

/**
 * A request's query parameters, derived from its URI's query string.
 *
 * @author Andy Wilkinson
 * @author Minju Kim
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
		int separator = parameter.indexOf('=');
		if (separator != -1) {
			parameters.add(decode(parameter.substring(0, separator)), decode(parameter.substring(separator + 1)));
		}
		else {
			parameters.add(decode(parameter), "");
		}
	}

	private static String decode(String encoded) {
		return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
	}

}
