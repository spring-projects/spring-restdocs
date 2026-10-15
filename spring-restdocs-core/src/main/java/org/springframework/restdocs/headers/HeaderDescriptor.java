/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.headers;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.snippet.AbstractDescriptor;

/**
 * A description of a header found in a request or response.
 *
 * @author Andreas Evers
 * @see HeaderDocumentation#headerWithName(String)
 */
public class HeaderDescriptor extends AbstractDescriptor<HeaderDescriptor> {

	private final String name;

	private boolean optional;

	/**
	 * Creates a new {@code HeaderDescriptor} describing the header with the given
	 * {@code name}.
	 * @param name the name
	 * @see HttpHeaders
	 */
	protected HeaderDescriptor(String name) {
		this.name = name;
	}

	/**
	 * Marks the header as optional.
	 * @return {@code this}
	 */
	public final HeaderDescriptor optional() {
		this.optional = true;
		return this;
	}

	/**
	 * Returns the name for the header.
	 * @return the header name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Returns {@code true} if the described header is optional, otherwise {@code false}.
	 * @return {@code true} if the described header is optional, otherwise {@code false}
	 */
	public final boolean isOptional() {
		return this.optional;
	}

}
