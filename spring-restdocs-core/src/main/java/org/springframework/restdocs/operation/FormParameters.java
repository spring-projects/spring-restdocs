/*
 * Copyright 2014-2022 the original author or authors.
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.springframework.util.LinkedMultiValueMap;

/**
 * A request's form parameters, derived from its form URL encoded body content.
 *
 * @author Andy Wilkinson
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
		try {
			return URLDecoder.decode(encoded, "UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException("Unable to URL decode " + encoded + " using UTF-8", ex);
		}

	}

}
