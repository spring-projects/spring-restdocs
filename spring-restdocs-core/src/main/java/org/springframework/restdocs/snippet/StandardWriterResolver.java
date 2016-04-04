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

package org.springframework.restdocs.snippet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

/**
 * Standard implementation of {@link WriterResolver}.
 *
 * @author Andy Wilkinson
 */
public final class StandardWriterResolver implements WriterResolver {

	private final PlaceholderResolver placeholderResolver;

	private final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper(
			"{", "}");

	private String encoding = "UTF-8";

	private TemplateFormat templateFormat;

	/**
	 * Creates a new {@code StandardWriterResolver} that will use the given
	 * {@code placeholderResolver} to resolve any placeholders in the
	 * {@code operationName}. Writers will use {@code UTF-8} encoding and, when writing to
	 * a file, will use a filename appropriate for Asciidoctor content.
	 *
	 * @param placeholderResolver the placeholder resolver
	 * @deprecated since 1.1.0 in favor of
	 * {@link #StandardWriterResolver(PropertyPlaceholderHelper.PlaceholderResolver, String, TemplateFormat)}
	 */
	@Deprecated
	public StandardWriterResolver(PlaceholderResolver placeholderResolver) {
		this(placeholderResolver, "UTF-8", TemplateFormats.asciidoctor());
	}

	/**
	 * Creates a new {@code StandardWriterResolver} that will use the given
	 * {@code placeholderResolver} to resolve any placeholders in the
	 * {@code operationName}. Writers will use the given {@code encoding} and, when
	 * writing to a file, will use a filename appropriate for content generated from
	 * templates in the given {@code templateFormat}.
	 *
	 * @param placeholderResolver the placeholder resolver
	 * @param encoding the encoding
	 * @param templateFormat the snippet format
	 */
	public StandardWriterResolver(PlaceholderResolver placeholderResolver,
			String encoding, TemplateFormat templateFormat) {
		this.placeholderResolver = placeholderResolver;
		this.encoding = encoding;
		this.templateFormat = templateFormat;
	}

	@Override
	public Writer resolve(String operationName, String snippetName,
			RestDocumentationContext context) throws IOException {
		File outputFile = resolveFile(
				this.propertyPlaceholderHelper.replacePlaceholders(operationName,
						this.placeholderResolver),
				snippetName + "." + this.templateFormat.getFileExtension(), context);

		if (outputFile != null) {
			createDirectoriesIfNecessary(outputFile);
			return new OutputStreamWriter(new FileOutputStream(outputFile),
					this.encoding);
		}
		else {
			return new OutputStreamWriter(System.out, this.encoding);
		}
	}

	@Override
	@Deprecated
	public void setEncoding(String encoding) {
		this.encoding = encoding;
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
