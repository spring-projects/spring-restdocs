/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.testfixtures.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Customizes the template that will be used when rendering a snippet in a
 * {@link RenderedSnippetTest rendered snippet test}.
 *
 * @author Andy Wilkinson
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SnippetTemplate {

	/**
	 * The name of the snippet whose template should be customized.
	 * @return the snippet name
	 */
	String snippet();

	/**
	 * The custom template to use when rendering the snippet.
	 * @return the custom template
	 */
	String template();

}
