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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.springframework.restdocs.operation.Parameters;

/**
 * A parser for the query string of a URI.
 *
 * @author Andy Wilkinson
 */
public class QueryStringParser {

	/**
	 * Parses the query string of the given {@code uri} and returns the resulting
	 * {@link Parameters}.
	 *
	 * @param uri the uri to parse
	 * @return the parameters parsed from the query string
	 */
	public Parameters parse(URI uri) {
		String query = uri.getRawQuery();
		if (query != null) {
			return parse(query);
		}
		return new Parameters();
	}

	private Parameters parse(String query) {
		Parameters parameters = new Parameters();
		try (Scanner scanner = new Scanner(query)) {
			scanner.useDelimiter("&");
			while (scanner.hasNext()) {
				processParameter(scanner.next(), parameters);
			}
		}
		return parameters;
	}

	private void processParameter(String parameter, Parameters parameters) {
		String[] components = parameter.split("=");
		if (components.length > 0 && components.length < 3) {
			if (components.length == 2) {
				String name = components[0];
				String value = components[1];
				parameters.add(decode(name), decode(value));
			}
			else {
				List<String> values = parameters.get(components[0]);
				if (values == null) {
					parameters.put(components[0], new LinkedList<String>());
				}
			}
		}
		else {
			throw new IllegalArgumentException(
					"The parameter '" + parameter + "' is malformed");
		}
	}

	private String decode(String encoded) {
		try {
			return URLDecoder.decode(encoded, "UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException(
					"Unable to URL encode " + encoded + " using UTF-8", ex);
		}

	}

}
