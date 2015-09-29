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
	 *
	 * @return {@code this}
	 */
	public final HeaderDescriptor optional() {
		this.optional = true;
		return this;
	}

	/**
	 * Returns the name for the header.
	 *
	 * @return the header name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Returns {@code true} if the described header is optional, otherwise {@code false}.
	 *
	 * @return {@code true} if the described header is optional, otherwise {@code false}
	 */
	public final boolean isOptional() {
		return this.optional;
	}

}
