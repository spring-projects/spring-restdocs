/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.snippet;

import java.io.IOException;
import java.io.Writer;

import org.springframework.restdocs.RestDocumentationContext;

/**
 * A {@code WriterResolver} is used to access the {@link Writer} that should be used to
 * write a snippet for an operation that is being documented.
 *
 * @author Andy Wilkinson
 */
public interface WriterResolver {

	/**
	 * Returns a writer that can be used to write the snippet with the given name for the
	 * operation with the given name.
	 * @param operationName the name of the operation that is being documented
	 * @param snippetName the name of the snippet
	 * @param restDocumentationContext the current documentation context
	 * @return the writer
	 * @throws IOException if a writer cannot be resolved
	 */
	Writer resolve(String operationName, String snippetName, RestDocumentationContext restDocumentationContext)
			throws IOException;

}
