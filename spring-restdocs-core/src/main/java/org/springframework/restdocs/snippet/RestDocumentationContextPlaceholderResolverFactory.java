/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.snippet;

import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

/**
 * A {@link PlaceholderResolverFactory} that creates
 * {@link RestDocumentationContextPlaceholderResolver} instances.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public final class RestDocumentationContextPlaceholderResolverFactory implements PlaceholderResolverFactory {

	@Override
	public PlaceholderResolver create(RestDocumentationContext context) {
		return new RestDocumentationContextPlaceholderResolver(context);
	}

}
