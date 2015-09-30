/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
	 *
	 * @param rel the rel of the link
	 */
	protected LinkDescriptor(String rel) {
		this.rel = rel;
	}

	/**
	 * Marks the link as optional.
	 *
	 * @return {@code this}
	 */
	public final LinkDescriptor optional() {
		this.optional = true;
		return this;
	}

	/**
	 * Returns the rel of the link described by this descriptor.
	 *
	 * @return the rel
	 */
	public final String getRel() {
		return this.rel;
	}

	/**
	 * Returns {@code true} if the described link is optional, otherwise {@code false}.
	 *
	 * @return {@code true} if the described link is optional, otherwise {@code false}
	 */
	public final boolean isOptional() {
		return this.optional;
	}

}
