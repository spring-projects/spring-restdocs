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

package org.springframework.restdocs.cookies;

import org.springframework.restdocs.snippet.IgnorableDescriptor;

/**
 * A description of a cookie found in a request or response.
 *
 * @author Andreas Evers
 * @author Clyde Stubbs
 * @since 2.1
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
