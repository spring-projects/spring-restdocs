/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.cookies;

import org.springframework.restdocs.snippet.IgnorableDescriptor;

/**
 * A description of a cookie found in a request or response.
 *
 * @author Clyde Stubbs
 * @author Andy Wilkinson
 * @since 3.0
 * @see CookieDocumentation#cookieWithName(String)
 */
public class CookieDescriptor extends IgnorableDescriptor<CookieDescriptor> {

	private final String name;

	private boolean optional;

	/**
	 * Creates a new {@code CookieDescriptor} describing the cookie with the given
	 * {@code name}.
	 * @param name the name
	 */
	protected CookieDescriptor(String name) {
		this.name = name;
	}

	/**
	 * Marks the cookie as optional.
	 * @return {@code this}
	 */
	public final CookieDescriptor optional() {
		this.optional = true;
		return this;
	}

	/**
	 * Returns the name for the cookie.
	 * @return the cookie name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Returns {@code true} if the described cookie is optional, otherwise {@code false}.
	 * @return {@code true} if the described cookie is optional, otherwise {@code false}
	 */
	public final boolean isOptional() {
		return this.optional;
	}

}
