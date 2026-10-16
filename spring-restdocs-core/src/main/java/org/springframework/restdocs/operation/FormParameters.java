/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Scanner;

import org.springframework.util.LinkedMultiValueMap;

/**
 * A request's form parameters, derived from its form URL encoded body content.
 *
 * @author Andy Wilkinson
 * @author Minju Kim
 * @since 3.0.0
 */
public final class FormParameters extends LinkedMultiValueMap<String, String> {

	private FormParameters() {

	}

	/**
	 * Extracts the form parameters from the body of the given {@code request}. If the
	 * request has no body content, an empty {@code FormParameters} is returned, rather
	 * than {@code null}.
	 * @param request the request
	 * @return the form parameters extracted from the body content
	 */
	public static FormParameters from(OperationRequest request) {
		return of(request.getContentAsString());
	}

	private static FormParameters of(String bodyContent) {
		if (bodyContent == null || bodyContent.length() == 0) {
			return new FormParameters();
		}
		return parse(bodyContent);
	}

	private static FormParameters parse(String bodyContent) {
		FormParameters parameters = new FormParameters();
		try (Scanner scanner = new Scanner(bodyContent)) {
			scanner.useDelimiter("&");
			while (scanner.hasNext()) {
				processParameter(scanner.next(), parameters);
			}
		}
		return parameters;
	}

	private static void processParameter(String parameter, FormParameters parameters) {
		int separator = parameter.indexOf('=');
		if (separator != -1) {
			parameters.add(decode(parameter.substring(0, separator)), decode(parameter.substring(separator + 1)));
		}
		else {
			parameters.add(decode(parameter), "");
		}
	}

	private static String decode(String encoded) {
		try {
			return URLDecoder.decode(encoded, "UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException("Unable to URL decode " + encoded + " using UTF-8", ex);
		}

	}

}
