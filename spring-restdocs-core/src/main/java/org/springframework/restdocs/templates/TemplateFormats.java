/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.templates;

/**
 * An enumeration of the built-in formats for which templates are provided.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public abstract class TemplateFormats {

	private static final TemplateFormat ASCIIDOCTOR = new AsciidoctorTemplateFormat();

	private static final TemplateFormat MARKDOWN = new MarkdownTemplateFormat();

	private TemplateFormats() {

	}

	/**
	 * Returns the Asciidoctor template format with the ID {@code asciidoctor} and the
	 * file extension {@code adoc}.
	 * @return the template format
	 */
	public static TemplateFormat asciidoctor() {
		return ASCIIDOCTOR;
	}

	/**
	 * Returns the Markdown template format with the ID {@code markdown} and the file
	 * extension {@code md}.
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
