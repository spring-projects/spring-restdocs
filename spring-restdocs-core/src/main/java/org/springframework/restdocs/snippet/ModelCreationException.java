/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.snippet;

/**
 * An exception that can be thrown by a {@link TemplatedSnippet} to indicate that a
 * failure has occurred during model creation.
 *
 * @author Andy Wilkinson
 * @see TemplatedSnippet#createModel(org.springframework.restdocs.operation.Operation)
 */
@SuppressWarnings("serial")
public class ModelCreationException extends RuntimeException {

	/**
	 * Creates a new {@code ModelCreationException} with the given {@code cause}.
	 * @param cause the cause
	 */
	public ModelCreationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new {@code ModelCreationException} with the given {@code message} and
	 * {@code cause}.
	 * @param message the message
	 * @param cause the cause
	 */
	public ModelCreationException(String message, Throwable cause) {
		super(message, cause);
	}

}
