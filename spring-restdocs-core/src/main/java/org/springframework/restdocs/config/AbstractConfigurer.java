/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.config;

import java.util.Map;

import org.springframework.restdocs.RestDocumentationContext;

/**
 * Abstract configurer that declares methods that are internal to the documentation
 * configuration implementation.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public abstract class AbstractConfigurer {

	/**
	 * Applies the configurer to the given {@code configuration}.
	 * @param configuration the configuration to be configured
	 * @param context the current documentation context
	 */
	public abstract void apply(Map<String, Object> configuration, RestDocumentationContext context);

}
