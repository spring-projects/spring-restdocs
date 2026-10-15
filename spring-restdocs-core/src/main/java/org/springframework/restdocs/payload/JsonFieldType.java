/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

import java.util.Locale;

import org.springframework.util.StringUtils;

/**
 * An enumeration of the possible types for a field in a JSON request or response payload.
 *
 * @author Andy Wilkinson
 */
public enum JsonFieldType {

	/**
	 * An array.
	 */
	ARRAY,

	/**
	 * A boolean value.
	 */
	BOOLEAN,

	/**
	 * An object (map).
	 */
	OBJECT,

	/**
	 * A number.
	 */
	NUMBER,

	/**
	 * {@code null}.
	 */
	NULL,

	/**
	 * A string.
	 */
	STRING,

	/**
	 * A variety of different types.
	 */
	VARIES;

	@Override
	public String toString() {
		return StringUtils.capitalize(this.name().toLowerCase(Locale.ENGLISH));
	}

}
