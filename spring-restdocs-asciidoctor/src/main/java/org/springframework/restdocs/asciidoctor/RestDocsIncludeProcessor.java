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

package org.springframework.restdocs.asciidoctor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.asciidoctor.ast.DocumentRuby;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;

/**
 * {@link IncludeProcessor} to include multiple REST docs snippets at once.
 * Every included snippet will get a section title with the (optional) defined level (defaults to 4).
 * <p>Example:
 * <pre>include::restdocs:sample-operation[snippets='response-fields, http-response',level=3]</pre>
 *
 * The processor includes the snippet files {@code response-fields.adoc} and {@code http-response.adoc}
 * from the {@code sample-operation} directory within the output directory.
 *
 * If a custom snippet (not in {@link CommonSnippet}) is defined in the <i>snippet</i> attribute
 * the processor will generate a custom title from the snippet name:
 * {@code custom-snippet} gets the title <i>Custom snippet</i>.
 *
 * @author Gerrit Meier
 */
public class RestDocsIncludeProcessor extends IncludeProcessor {

	private static final String REST_DOCS_SCHEMA = "restdocs:";
	private static final String SNIPPETS_ATTRIBUTE = "snippets";
	private static final String SNIPPETS_DELIMITER = ",";
	private static final int INCLUDE_START_LINE_NUMBER = 1;
	private static final int DEFAULT_SECTION_LEVEL = 4;

	/**
	 * Must have constructor to be compatible with asciidoctor(j).
	 *
	 * @param config - the configuration provided by asciidoctor
	 */
	public RestDocsIncludeProcessor(Map<String, Object> config) {
		super(config);
	}

	@Override
	public boolean handles(String target) {
		return target != null && target.startsWith(REST_DOCS_SCHEMA);
	}

	@Override
	public void process(DocumentRuby document, PreprocessorReader reader,
						String operation, Map<String, Object> attributes) {

		SnippetFileReader snippetFileReader = new SnippetFileReader(operation, document.getAttributes());
		int sectionLevel = getSectionLevel(attributes);

		StringBuilder resultingContent = new StringBuilder();
		for (Snippet snippet : getSnippetsToInclude(attributes)) {
			SnippetInclude snippetInclude = new SnippetInclude(sectionLevel, snippetFileReader.readSnippet(snippet));
			resultingContent.append(snippetInclude.getSectionWithTitle(snippet));
		}
		reader.push_include(resultingContent.toString(), operation, operation, INCLUDE_START_LINE_NUMBER, attributes);
	}

	private int getSectionLevel(Map<String, Object> attributes) {
		Object levelFromAttributes = attributes.get("level");
		if (levelFromAttributes == null) {
			return DEFAULT_SECTION_LEVEL;
		}

		return Integer.parseInt(levelFromAttributes.toString());
	}

	private List<Snippet> getSnippetsToInclude(Map<String, Object> attributes) {
		if (hasSnippetsAttribute(attributes)) {
			List<String> snippetNames = getDefinedSnippetsNames(attributes);
			return convertToSnippet(snippetNames);
		}

		return Collections.emptyList();
	}

	private boolean hasSnippetsAttribute(Map<String, Object> attributes) {
		return attributes.containsKey(SNIPPETS_ATTRIBUTE);
	}

	private List<String> getDefinedSnippetsNames(Map<String, Object> attributes) {
		return Arrays.asList(
				attributes.get(SNIPPETS_ATTRIBUTE).toString().split(SNIPPETS_DELIMITER));
	}

	private List<Snippet> convertToSnippet(List<String> snippetNames) {
		List<Snippet> snippets = new ArrayList<>();
		for (String snippetName : snippetNames) {
			if (isSnippetNameEmpty(snippetName)) {
				continue;
			}
			snippets.add(resolveSnippet(snippetName));
		}
		return snippets;
	}

	private boolean isSnippetNameEmpty(String snippetName) {
		return snippetName.trim().length() == 0;
	}

	private Snippet resolveSnippet(String name) {
		for (CommonSnippet snippet : CommonSnippet.values()) {
			if (snippet.getName().equals(name.trim())) {
				return snippet;
			}
		}
		return new UserDefinedSnippet(name);
	}

	/**
	 * Include the content of the file provided by {@link SnippetFileReader}
	 * and generate a title with the given section level.
	 */
	final class SnippetInclude {
		private static final String LINEFEED = "\n";
		private static final String HEADLINE_SYMBOL = "=";

		private final int level;
		private final String content;

		SnippetInclude(int level, String content) {
			this.level = level;
			this.content = content;
		}

		String getSectionWithTitle(Snippet snippet) {
			return getSectionTitle(snippet) +
					getSectionContent();
		}

		private String getSectionContent() {
			return this.content + LINEFEED;
		}

		private String getSectionTitle(Snippet snippet) {
			return createSectionLevel(this.level) +
					snippet.getTitle() +
					LINEFEED;
		}

		private String createSectionLevel(int level) {
			if (level == 0) {
				return " ";
			}

			return HEADLINE_SYMBOL + createSectionLevel(level - 1);
		}
	}

	/**
	 * Determines the file path of the snippet and reads its content.
	 * Depends on the document attribute <i>snippets</i> provided by
	 * {@link DefaultAttributesPreprocessor} to find the right output directory.
	 */
	final class SnippetFileReader {

		private final String identifier;
		private final String documentDirectory;
		private final String snippetsDirectory;

		SnippetFileReader(String identifier, Map<String, Object> attributes) {
			this.identifier = identifier.substring(REST_DOCS_SCHEMA.length());
			this.documentDirectory = (String) attributes.get("docdir");
			this.snippetsDirectory = ((File) attributes.get("snippets")).getPath();
		}

		String readSnippet(Snippet snippet) {
			final String snippetPath = this.documentDirectory + File.separator +
					this.snippetsDirectory + File.separator +
					this.identifier + File.separator +
					snippet.getFileName();
			try {
				return new String(Files.readAllBytes(Paths.get(snippetPath)));
			}
			catch (IOException e) {
				return "WARNING: Snippet not found at " + snippetPath;
			}
		}
	}

	/**
	 * Plain interface to define the needed API of a snippet
	 * to get included by the processor.
	 */
	interface Snippet {
		String FILE_EXTENSION = ".adoc";

		String getTitle();

		String getFileName();
	}

	/**
	 * Simple {@code Snippet} implementation to provide support for snippets
	 * that are not defined in {@link CommonSnippet}.
	 */
	final class UserDefinedSnippet implements Snippet {
		private final String title;
		private final String name;

		UserDefinedSnippet(String name) {
			this.title = createTitleFromName(name);
			this.name = name;
		}

		@Override
		public String getTitle() {
			return this.title;
		}

		@Override
		public String getFileName() {
			return this.name + FILE_EXTENSION;
		}

		private String createTitleFromName(String name) {
			return name.substring(0, 1).toUpperCase() + name.substring(1).replace("-", " ");
		}
	}

	/**
	 * Commonly used snippets.
	 */
	enum CommonSnippet implements Snippet {
		CURL_REQUEST("curl request", "curl-request"),
		HTTP_REQUEST("HTTP request", "http-request"),
		HTTPIE_REQUEST("HTTPie request", "httpie-request"),
		REQUEST_BODY("Request body", "request-body"),
		REQUEST_FIELDS("Request fields", "request-fields"),

		HTTP_RESPONSE("HTTP response", "http-response"),
		RESPONSE_BODY("Response body", "response-body"),
		RESPONSE_FIELDS("Response fields", "response-fields"),

		LINKS("Links", "links");

		private final String title;
		private final String name;

		CommonSnippet(String title, String name) {
			this.title = title;
			this.name = name;
		}

		@Override
		public String getTitle() {
			return this.title;
		}

		@Override
		public String getFileName() {
			return getName() + FILE_EXTENSION;
		}

		public String getName() {
			return this.name;
		}

	}

}
