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

/**
 * An enumeration of the built-in snippet formats.
 *
 * @author Andy Wilkinson
 */
public abstract class SnippetFormats {

	private static final SnippetFormat ASCIIDOCTOR = new AsciidoctorSnippetFormat();

	private static final SnippetFormat MARKDOWN = new MarkdownSnippetFormat();

	private SnippetFormats() {

	}

	/**
	 * Returns the Asciidoctor snippet format.
	 *
	 * @return the snippet format
	 */
	public static SnippetFormat asciidoctor() {
		return ASCIIDOCTOR;
	}

	/**
	 * Returns the Markdown snippet format.
	 *
	 * @return the snippet format
	 */
	public static SnippetFormat markdown() {
		return MARKDOWN;
	}

	private static final class AsciidoctorSnippetFormat implements SnippetFormat {

		private static final String FILE_EXTENSION = "adoc";

		@Override
		public String getFileExtension() {
			return FILE_EXTENSION;
		}

	}

	private static final class MarkdownSnippetFormat implements SnippetFormat {

		private static final String FILE_EXTENSION = "md";

		@Override
		public String getFileExtension() {
			return FILE_EXTENSION;
		}

	}

}
