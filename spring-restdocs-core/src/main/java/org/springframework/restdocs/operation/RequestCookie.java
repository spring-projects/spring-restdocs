/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

/**
 * A representation of a Cookie received in a request.
 *
 * @author Andy Wilkinson
 * @since 1.2.0
 */
public final class RequestCookie {

	private final String name;

	private final String value;

	/**
	 * Creates a new {@code RequestCookie} with the given {@code name} and {@code value}.
	 * @param name the name of the cookie
	 * @param value the value of the cookie
	 */
	public RequestCookie(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Returns the name of the cookie.
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the value of the cookie.
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

}
