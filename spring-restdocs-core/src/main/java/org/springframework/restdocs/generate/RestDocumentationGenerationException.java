/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.generate;

/**
 * An exception that can be thrown when a failure occurs during REST documentation
 * generation.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public class RestDocumentationGenerationException extends RuntimeException {

	/**
	 * Creates a new {@code RestDocumentationException} with the given {@code cause}.
	 * @param cause the cause
	 */
	public RestDocumentationGenerationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new {@code RestDocumentationException} with the given {@code message} and
	 * {@code cause}.
	 * @param message the message
	 * @param cause the cause
	 */
	public RestDocumentationGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

}
