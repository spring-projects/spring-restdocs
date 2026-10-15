/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

/**
 * Thrown to indicate that a failure has occurred during payload handling.
 *
 * @author Andy Wilkinson
 *
 */
@SuppressWarnings("serial")
class PayloadHandlingException extends RuntimeException {

	/**
	 * Creates a new {@code PayloadHandlingException} with the given {@code message}.
	 * @param message the message
	 * @since 1.2.0
	 */
	PayloadHandlingException(String message) {
		super(message);
	}

	/**
	 * Creates a new {@code PayloadHandlingException} with the given {@code cause}.
	 * @param cause the cause of the failure
	 */
	PayloadHandlingException(Throwable cause) {
		super(cause);
	}

}
