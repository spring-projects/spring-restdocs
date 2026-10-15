/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.hypermedia;

import org.springframework.restdocs.snippet.IgnorableDescriptor;

/**
 * A description of a link found in a hypermedia API.
 *
 * @author Andy Wilkinson
 * @see HypermediaDocumentation#linkWithRel(String)
 */
public class LinkDescriptor extends IgnorableDescriptor<LinkDescriptor> {

	private final String rel;

	private boolean optional;

	/**
	 * Creates a new {@code LinkDescriptor} describing a link with the given {@code rel}.
	 * @param rel the rel of the link
	 */
	protected LinkDescriptor(String rel) {
		this.rel = rel;
	}

	/**
	 * Marks the link as optional.
	 * @return {@code this}
	 */
	public final LinkDescriptor optional() {
		this.optional = true;
		return this;
	}

	/**
	 * Returns the rel of the link described by this descriptor.
	 * @return the rel
	 */
	public final String getRel() {
		return this.rel;
	}

	/**
	 * Returns {@code true} if the described link is optional, otherwise {@code false}.
	 * @return {@code true} if the described link is optional, otherwise {@code false}
	 */
	public final boolean isOptional() {
		return this.optional;
	}

}
