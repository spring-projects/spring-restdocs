/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.templates;

/**
 * A {@link TemplateFormat} provides information about a particular template format, such
 * as Asciidoctor or Markdown.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public interface TemplateFormat {

	/**
	 * Returns the id of this template format.
	 * @return the id
	 */
	String getId();

	/**
	 * Returns the file extension to use for files generated from templates in this
	 * format.
	 * @return the file extension
	 */
	String getFileExtension();

}
