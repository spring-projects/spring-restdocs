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
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.restdocs.snippet.SnippetGenerationException;
import org.springframework.restdocs.snippet.SnippetWritingResultHandler;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

/**
 * A {@link SnippetWritingResultHandler} that produces a snippet documenting the path
 * parameters supported by a RESTful resource.
 *
 * @author Andy Wilkinson
 */
public class PathParametersSnippetResultHandler extends
		AbstractParametersSnippetResultHandler {

	private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

	protected PathParametersSnippetResultHandler(String identifier,
			Map<String, Object> attributes, ParameterDescriptor... descriptors) {
		super(identifier, "path-parameters", attributes, descriptors);
	}

	@Override
	protected Set<String> extractActualParameters(MvcResult result) {
		String urlTemplate = extractUrlTemplate(result);
		Matcher matcher = NAMES_PATTERN.matcher(urlTemplate);
		Set<String> actualParameters = new HashSet<>();
		while (matcher.find()) {
			String match = matcher.group(1);
			actualParameters.add(getParameterName(match));
		}
		return actualParameters;
	}

	private String extractUrlTemplate(MvcResult result) {
		String urlTemplate = (String) result.getRequest().getAttribute(
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
		throw new SnippetGenerationException(message);
	}

}
