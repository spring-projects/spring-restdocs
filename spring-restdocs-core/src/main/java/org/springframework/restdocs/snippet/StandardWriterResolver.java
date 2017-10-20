/*
 * Copyright 2014-2017 the original author or authors.
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

/**
 * Standard implementation of {@link WriterResolver}.
 *
 * @author Andy Wilkinson
 */
public final class StandardWriterResolver implements WriterResolver {

	private final PlaceholderResolverFactory placeholderResolverFactory;

	private final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper(
			"{", "}");

	private String encoding = "UTF-8";

	private TemplateFormat templateFormat;

	/**
	 * Creates a new {@code StandardWriterResolver} that will use a
	 * {@link PlaceholderResolver} created from the given
	 * {@code placeholderResolverFactory} to resolve any placeholders in the
	 * {@code operationName}. Writers will use the given {@code encoding} and, when
	 * writing to a file, will use a filename appropriate for content generated from
	 * templates in the given {@code templateFormat}.
	 *
	 * @param placeholderResolverFactory the placeholder resolver factory
	 * @param encoding the encoding
	 * @param templateFormat the snippet format
	 */
	public StandardWriterResolver(PlaceholderResolverFactory placeholderResolverFactory,
			String encoding, TemplateFormat templateFormat) {
		this.placeholderResolverFactory = placeholderResolverFactory;
		this.encoding = encoding;
		this.templateFormat = templateFormat;
	}

	@Override
	public Writer resolve(String operationName, String snippetName,
			RestDocumentationContext context) throws IOException {
		PlaceholderResolver placeholderResolver = this.placeholderResolverFactory
				.create(context);
		String outputDirectory = replacePlaceholders(placeholderResolver, operationName);
		String fileName = replacePlaceholders(placeholderResolver, snippetName) + "."
				+ this.templateFormat.getFileExtension();
		File outputFile = resolveFile(outputDirectory, fileName, context);
		if (outputFile != null) {
			createDirectoriesIfNecessary(outputFile);
			return new OutputStreamWriter(new FileOutputStream(outputFile),
					this.encoding);
		}
		else {
			return new OutputStreamWriter(System.out, this.encoding);
		}
	}

	private String replacePlaceholders(PlaceholderResolver resolver, String input) {
		return this.propertyPlaceholderHelper.replacePlaceholders(input, resolver);
	}

	File resolveFile(String outputDirectory, String fileName,
			RestDocumentationContext context) {
		File outputFile = new File(outputDirectory, fileName);
		if (!outputFile.isAbsolute()) {
			outputFile = makeRelativeToConfiguredOutputDir(outputFile, context);
		}
		return outputFile;
	}

	private File makeRelativeToConfiguredOutputDir(File outputFile,
			RestDocumentationContext context) {
		File configuredOutputDir = context.getOutputDirectory();
		if (configuredOutputDir != null) {
			return new File(configuredOutputDir, outputFile.getPath());
		}
		return null;
	}

	private void createDirectoriesIfNecessary(File outputFile) {
		File parent = outputFile.getParentFile();
		if (!parent.isDirectory() && !parent.mkdirs()) {
			throw new IllegalStateException(
					"Failed to create directory '" + parent + "'");
		}
	}

}
