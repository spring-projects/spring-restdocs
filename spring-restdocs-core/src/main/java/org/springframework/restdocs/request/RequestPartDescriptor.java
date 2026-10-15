/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.request;

import org.springframework.restdocs.snippet.IgnorableDescriptor;

/**
 * A descriptor of a request part.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 * @see RequestDocumentation#partWithName
 */
public class RequestPartDescriptor extends IgnorableDescriptor<RequestPartDescriptor> {

	private final String name;

	private boolean optional;

	/**
	 * Creates a new {@code RequestPartDescriptor} describing the request part with the
	 * given {@code name}.
	 * @param name the name of the request part
	 */
	protected RequestPartDescriptor(String name) {
		this.name = name;
	}

	/**
	 * Marks the request part as optional.
	 * @return {@code this}
	 */
	public final RequestPartDescriptor optional() {
		this.optional = true;
		return this;
	}

	/**
	 * Returns the name of the request part being described by this descriptor.
	 * @return the name of the parameter
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Returns {@code true} if the described request part is optional, otherwise
	 * {@code false}.
	 * @return {@code true} if the described request part is optional, otherwise
	 * {@code false}
	 */
	public final boolean isOptional() {
		return this.optional;
	}

}
