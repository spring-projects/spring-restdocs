/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

/**
 * A {@code FieldTypesDoNotMatchException} is thrown when the documented and actual types
 * of a field do not match.
 *
 * @author Andy Wilkinson
 */
class FieldTypesDoNotMatchException extends RuntimeException {

	/**
	 * Creates a new {@code FieldTypesDoNotMatchException} for the field described by the
	 * given {@code fieldDescriptor} that has the given {@code actualType}.
	 * @param fieldDescriptor the field
	 * @param actualType the actual type of the field
	 */
	FieldTypesDoNotMatchException(FieldDescriptor fieldDescriptor, Object actualType) {
		super("The documented type of the field '" + fieldDescriptor.getPath() + "' is " + fieldDescriptor.getType()
				+ " but the actual type is " + actualType);
	}

}
