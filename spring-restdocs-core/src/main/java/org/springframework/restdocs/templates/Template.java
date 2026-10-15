/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.templates;

import java.util.Map;

/**
 * A compiled {@code Template} that can be rendered to a {@link String}.
 *
 * @author Andy Wilkinson
 *
 */
public interface Template {

	/**
	 * Renders the template to a {@link String} using the given {@code context} for
	 * variable/property resolution.
	 * @param context the context to use
	 * @return the rendered template
	 */
	String render(Map<String, Object> context);

}
