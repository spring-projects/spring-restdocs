/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.templates;

import java.io.IOException;

import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

/**
 * A {@code TemplateEngine} is used to render documentation snippets.
 *
 * @author Andy Wilkinson
 * @see MustacheTemplateEngine
 */
public interface TemplateEngine {

	/**
	 * Compiles the template at the given {@code path}. Typically, a
	 * {@link TemplateResourceResolver} will be used to resolve the path into a resource
	 * that can be read and compiled.
	 * @param path the path of the template
	 * @return the compiled {@code Template}
	 * @throws IOException if compilation fails
	 */
	Template compileTemplate(String path) throws IOException;

}
