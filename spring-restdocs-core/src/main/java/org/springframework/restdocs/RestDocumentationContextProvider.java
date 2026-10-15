/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs;

/**
 * A {@code RestDocumentationContextProvider} is used to provide access to the
 * {@link RestDocumentationContext}.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public interface RestDocumentationContextProvider {

	/**
	 * Returns a {@link RestDocumentationContext} for the operation that is about to be
	 * performed.
	 * @return the context for the operation
	 */
	RestDocumentationContext beforeOperation();

}
