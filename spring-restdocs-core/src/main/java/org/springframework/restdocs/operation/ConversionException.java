/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

/**
 * An exception that can be thrown by {@link RequestConverter} and
 * {@link ResponseConverter} implementations to indicate that a failure has occurred
 * during conversion.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 * @see RequestConverter#convert(Object)
 * @see ResponseConverter#convert(Object)
 */
public class ConversionException extends RuntimeException {

	/**
	 * Creates a new {@code ConversionException} with the given {@code cause}.
	 * @param cause the cause
	 */
	public ConversionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new {@code ConversionException} with the given {@code message} and
	 * {@code cause}.
	 * @param message the message
	 * @param cause the cause
	 */
	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

}
