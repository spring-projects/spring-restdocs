/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.config;

import org.springframework.restdocs.templates.TemplateFormat;

/**
 * An encapsulation of the configuration for documentation snippets.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
class SnippetConfiguration {

	private final String encoding;

	private final TemplateFormat format;

	SnippetConfiguration(String encoding, TemplateFormat templateFormat) {
		this.encoding = encoding;
		this.format = templateFormat;
	}

	String getEncoding() {
		return this.encoding;
	}

	TemplateFormat getTemplateFormat() {
		return this.format;
	}

}
