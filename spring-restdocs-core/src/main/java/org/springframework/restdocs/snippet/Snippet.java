/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.snippet;

import java.io.IOException;

import org.springframework.restdocs.operation.Operation;

/**
 * A {@link Snippet} is used to document aspects of a call to a RESTful API.
 *
 * @author Andy Wilkinson
 */
public interface Snippet {

	/**
	 * Documents the call to the RESTful API described by the given {@code operation}.
	 * @param operation the API operation
	 * @throws IOException if a failure occurs will documenting the operation
	 */
	void document(Operation operation) throws IOException;

}
