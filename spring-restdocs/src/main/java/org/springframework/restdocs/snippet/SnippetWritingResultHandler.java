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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.restdocs.config.RestDocumentationContext;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

/**
 * Base class for a {@link ResultHandler} that writes a documentation snippet
 * 
 * @author Andy Wilkinson
 */
public abstract class SnippetWritingResultHandler implements ResultHandler {

	private final Map<String, Object> attributes = new HashMap<>();

	private final String outputDir;

	private final String fileName;

	protected SnippetWritingResultHandler(String outputDir, String fileName,
			Map<String, Object> attributes) {
		this.outputDir = outputDir;
		this.fileName = fileName;
		if (attributes != null) {
			this.attributes.putAll(attributes);
		}
	}

	protected abstract void handle(MvcResult result, PrintWriter writer)
			throws IOException;

	@Override
	public void handle(MvcResult result) throws IOException {
		try (Writer writer = createWriter()) {
			handle(result, new PrintWriter(writer));
		}
	}

	protected Map<String, Object> getAttributes() {
		return this.attributes;
	}

	private Writer createWriter() throws IOException {
		File outputFile = new OutputFileResolver().resolve(this.outputDir, this.fileName
				+ ".adoc");

		if (outputFile != null) {
			File parent = outputFile.getParentFile();
			if (!parent.isDirectory() && !parent.mkdirs()) {
				throw new IllegalStateException("Failed to create directory '" + parent
						+ "'");
			}
			RestDocumentationContext context = RestDocumentationContext.currentContext();
			if (context == null || context.getSnippetEncoding() == null) {
				return new FileWriter(outputFile);
			}
			return new OutputStreamWriter(new FileOutputStream(outputFile),
					context.getSnippetEncoding());
		}
		else {
			return new OutputStreamWriter(System.out);
		}
	}

}