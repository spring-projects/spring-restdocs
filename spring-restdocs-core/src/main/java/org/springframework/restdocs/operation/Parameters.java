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

package org.springframework.restdocs.operation;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

/**
 * The parameters received in a request.
 *
 * @author Andy Wilkinson
 */
@SuppressWarnings("serial")
public class Parameters extends LinkedMultiValueMap<String, String> {

	/**
	 * Converts the parameters to a query string suitable for use in a URI or the body of
	 * a form-encoded request.
	 *
	 * @return the query string
	 */
	public String toQueryString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, List<String>> entry : entrySet()) {
			if (entry.getValue().isEmpty()) {
				append(sb, entry.getKey());
			}
			else {
				for (String value : entry.getValue()) {
					append(sb, entry.getKey(), value);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Returns a new {@code Parameters} containing only the parameters that do no appear
	 * in the query string of the given {@code uri}.
	 *
	 * @param uri the uri
	 * @return the unique parameters
	 */
	public Parameters getUniqueParameters(URI uri) {
		Parameters queryStringParameters = new QueryStringParser().parse(uri);
		Parameters uniqueParameters = new Parameters();

		for (Map.Entry<String, List<String>> parameter : entrySet()) {
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

	private static void append(StringBuilder sb, String key) {
		append(sb, key, "");
	}

	private static void append(StringBuilder sb, String key, String value) {
		doAppend(sb, urlEncodeUTF8(key) + "=" + urlEncodeUTF8(value));
	}

	private static void doAppend(StringBuilder sb, String toAppend) {
		if (sb.length() > 0) {
			sb.append("&");
		}
		sb.append(toAppend);
	}

	private static String urlEncodeUTF8(String s) {
		if (!StringUtils.hasLength(s)) {
			return "";
		}
		try {
			return URLEncoder.encode(s, "UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException("Unable to URL encode " + s + " using UTF-8",
					ex);
		}
	}

}
