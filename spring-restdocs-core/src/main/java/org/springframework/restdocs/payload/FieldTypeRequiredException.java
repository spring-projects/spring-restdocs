/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

/**
 * A {@code FieldTypeRequiredException} is thrown when a field's type cannot be determined
 * automatically and, therefore, must be explicitly provided.
 *
 * @author Andy Wilkinson
 */
@SuppressWarnings("serial")
public class FieldTypeRequiredException extends RuntimeException {

	/**
	 * Creates a new {@code FieldTypeRequiredException} indicating that a type is required
	 * for the reason described in the given {@code message}.
	 * @param message the message
	 */
	public FieldTypeRequiredException(String message) {
		super(message);
	}

}
