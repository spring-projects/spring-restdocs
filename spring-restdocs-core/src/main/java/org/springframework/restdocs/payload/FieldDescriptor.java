/*
 * Copyright 2014-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.payload;

import org.jspecify.annotations.Nullable;

import org.springframework.restdocs.snippet.IgnorableDescriptor;
import org.springframework.util.Assert;

/**
 * A description of a field found in a request or response payload.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 * @see PayloadDocumentation#fieldWithPath(String)
 */
public class FieldDescriptor extends IgnorableDescriptor<FieldDescriptor> {

	private final String path;

	private @Nullable Object type;

	private boolean optional;

	/**
	 * Creates a new {@code FieldDescriptor} describing the field with the given
	 * {@code path}.
	 * @param path the path
	 */
	protected FieldDescriptor(String path) {
		Assert.notNull(path, "Path must not be null");
		this.path = path;
	}

	/**
	 * Specifies the type of the field. When documenting a JSON payload, the
	 * {@link JsonFieldType} enumeration will typically be used.
	 * @param type the type of the field
	 * @return {@code this}
	 * @see JsonFieldType
	 */
	public final FieldDescriptor type(@Nullable Object type) {
		this.type = type;
		return this;
	}

	/**
	 * Marks the field as optional.
	 * @return {@code this}
	 */
	public final FieldDescriptor optional() {
		this.optional = true;
		return this;
	}

	/**
	 * Returns the path of the field described by this descriptor.
	 * @return the path
	 */
	public final String getPath() {
		return this.path;
	}

	/**
	 * Returns the type of the field described by this descriptor.
	 * @return the type
	 */
	public final @Nullable Object getType() {
		return this.type;
	}

	/**
	 * Returns {@code true} if the described field is optional, otherwise {@code false}.
	 * @return {@code true} if the described field is optional, otherwise {@code false}
	 */
	public final boolean isOptional() {
		return this.optional;
	}

}
