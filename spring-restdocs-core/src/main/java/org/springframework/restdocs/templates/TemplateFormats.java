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

package org.springframework.restdocs.templates;

/**
 * An enumeration of the built-in formats for which templates are provuded.
 *
 * @author Andy Wilkinson
 */
public abstract class TemplateFormats {

	private static final TemplateFormat ASCIIDOCTOR = new AsciidoctorTemplateFormat();

	private static final TemplateFormat MARKDOWN = new MarkdownTemplateFormat();

	private TemplateFormats() {

	}

	/**
	 * Returns the Asciidoctor template format with the ID {@code asciidoctor} and the
	 * file extension {@code adoc}.
	 *
	 * @return the template format
	 */
	public static TemplateFormat asciidoctor() {
		return ASCIIDOCTOR;
	}

	/**
	 * Returns the Markdown template format with the ID {@code markdown} and the file
	 * extension {@code md}.
	 *
	 * @return the template format
	 */
	public static TemplateFormat markdown() {
		return MARKDOWN;
	}

	private abstract static class AbstractTemplateFormat implements TemplateFormat {

		private final String name;

		private final String fileExtension;

		private AbstractTemplateFormat(String name, String fileExtension) {
			this.name = name;
			this.fileExtension = fileExtension;
		}

		@Override
		public String getId() {
			return this.name;
		}

		@Override
		public String getFileExtension() {
			return this.fileExtension;
		}

	}

	private static final class AsciidoctorTemplateFormat extends AbstractTemplateFormat {

		private AsciidoctorTemplateFormat() {
			super("asciidoctor", "adoc");
		}

	}

	private static final class MarkdownTemplateFormat extends AbstractTemplateFormat {

		private MarkdownTemplateFormat() {
			super("markdown", "md");
		}

	}

}
