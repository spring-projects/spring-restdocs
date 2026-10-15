/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

/**
 * A {@code FieldDoesNotExistException} is thrown when a requested field does not exist in
 * a payload.
 *
 * @author Andy Wilkinson
 */
@SuppressWarnings("serial")
public class FieldDoesNotExistException extends RuntimeException {

	/**
	 * Creates a new {@code FieldDoesNotExistException} that indicates that the field with
	 * the given {@code fieldPath} does not exist.
	 * @param fieldPath the path of the field that does not exist
	 */
	public FieldDoesNotExistException(String fieldPath) {
		super("The payload does not contain a field with the path '" + fieldPath + "'");
	}

}
