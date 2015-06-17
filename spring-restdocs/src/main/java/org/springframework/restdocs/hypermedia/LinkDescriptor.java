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

/**
 * A description of a link found in a hypermedia API
 * 
 * @see HypermediaDocumentation#linkWithRel(String)
 * 
 * @author Andy Wilkinson
 */
public class LinkDescriptor {

	private final String rel;

	private String description;

	private boolean optional;

	LinkDescriptor(String rel) {
		this.rel = rel;
	}

	/**
	 * Specifies the description of the link
	 * 
	 * @param description The link's description
	 * @return {@code this}
	 */
	public LinkDescriptor description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Marks the link as optional
	 *
	 * @return {@code this}
	 */
	public LinkDescriptor optional() {
		this.optional = true;
		return this;
	}

	String getRel() {
		return this.rel;
	}

	String getDescription() {
		return this.description;
	}

	boolean isOptional() {
		return this.optional;
	}
}
