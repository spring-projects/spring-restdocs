/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.request;

import org.springframework.restdocs.snippet.IgnorableDescriptor;

/**
 * A descriptor of a request or path parameter.
 *
 * @author Andy Wilkinson
 * @see RequestDocumentation#parameterWithName
 *
 */
public class ParameterDescriptor extends IgnorableDescriptor<ParameterDescriptor> {

	private final String name;

	private boolean optional;

	/**
	 * Creates a new {@code ParameterDescriptor} describing the parameter with the given
	 * {@code name}.
	 * @param name the name of the parameter
	 */
	protected ParameterDescriptor(String name) {
		this.name = name;
	}

	/**
	 * Marks the parameter as optional.
	 * @return {@code this}
	 */
	public final ParameterDescriptor optional() {
		this.optional = true;
		return this;
	}

	/**
	 * Returns the name of the parameter being described by this descriptor.
	 * @return the name of the parameter
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Returns {@code true} if the described parameter is optional, otherwise
	 * {@code false}.
	 * @return {@code true} if the described parameter is optional, otherwise
	 * {@code false}
	 */
	public final boolean isOptional() {
		return this.optional;
	}

}
