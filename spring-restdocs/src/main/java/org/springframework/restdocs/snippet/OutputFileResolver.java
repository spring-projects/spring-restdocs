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

package org.springframework.restdocs.snippet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.restdocs.config.RestDocumentationContext;

/**
 * {@code OutputFileResolver} resolves an absolute output file based on the current
 * configuration and context.
 * 
 * @author Andy Wilkinson
 */
class OutputFileResolver {

	private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([A-Z])");

	File resolve(String outputDirectory, String fileName) {
		Map<String, String> replacements = createReplacements();
		String path = outputDirectory;
		for (Entry<String, String> replacement : replacements.entrySet()) {
			while (path.contains(replacement.getKey())) {
				if (replacement.getValue() == null) {
					throw new IllegalStateException("No replacement is available for "
							+ replacement.getKey());
				}
				else {
					path = path.replace(replacement.getKey(), replacement.getValue());
				}
			}
		}

		File outputFile = new File(path, fileName);
		if (!outputFile.isAbsolute()) {
			outputFile = makeRelativeToConfiguredOutputDir(outputFile);
		}
		return outputFile;
	}

	private Map<String, String> createReplacements() {
		RestDocumentationContext context = RestDocumentationContext.currentContext();

		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put("{methodName}", context == null ? null : context.getTestMethod()
				.getName());
		replacements.put("{method-name}", context == null ? null
				: camelCaseToDash(context.getTestMethod().getName()));
		replacements.put("{method_name}", context == null ? null
				: camelCaseToUnderscore(context.getTestMethod().getName()));
		replacements.put("{step}",
				context == null ? null : Integer.toString(context.getStepCount()));

		return replacements;
	}

	private String camelCaseToDash(String string) {
		return camelCaseToSeparator(string, "-");
	}

	private String camelCaseToUnderscore(String string) {
		return camelCaseToSeparator(string, "_");
	}

	private String camelCaseToSeparator(String string, String separator) {
		Matcher matcher = CAMEL_CASE_PATTERN.matcher(string);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(result, separator + matcher.group(1).toLowerCase());
		}
		matcher.appendTail(result);
		return result.toString();
	}

	private File makeRelativeToConfiguredOutputDir(File outputFile) {
		File configuredOutputDir = new DocumentationProperties().getOutputDir();
		if (configuredOutputDir != null) {
			return new File(configuredOutputDir, outputFile.getPath());
		}
		return null;
	}
}
