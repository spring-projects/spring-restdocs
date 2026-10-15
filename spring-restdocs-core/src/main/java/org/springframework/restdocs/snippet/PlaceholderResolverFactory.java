/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.snippet;

import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

/**
 * A factory for creating {@link PlaceholderResolver} instances.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public interface PlaceholderResolverFactory {

	/**
	 * Creates a new {@link PlaceholderResolver} using the given {@code context}.
	 * @param context the context
	 * @return the placeholder resolver
	 */
	PlaceholderResolver create(RestDocumentationContext context);

}
