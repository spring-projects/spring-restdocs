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

package org.springframework.restdocs.snippet;

import java.util.HashMap;
import java.util.Map;

import org.springframework.restdocs.snippet.Attributes.Attribute;

/**
 * Base class for descriptors. Provides the ability to associate arbitrary attributes with
 * a descriptor.
 *
 * @param <T> the type of the descriptor
 * @author Andy Wilkinson
 */
public abstract class AbstractDescriptor<T extends AbstractDescriptor<T>> {

	private Map<String, Object> attributes = new HashMap<>();

	private Object description;

	/**
	 * Adds the given {@code attributes} to the descriptor.
	 *
	 * @param attributes the attributes
	 * @return the descriptor
	 */
	@SuppressWarnings("unchecked")
	public final T attributes(Attribute... attributes) {
		for (Attribute attribute : attributes) {
			this.attributes.put(attribute.getKey(), attribute.getValue());
		}
		return (T) this;
	}

	/**
	 * Specifies the description.
	 *
	 * @param description the description
	 * @return the descriptor
	 */
	@SuppressWarnings("unchecked")
	public final T description(Object description) {
		this.description = description;
		return (T) this;
	}

	/**
	 * Returns the description.
	 *
	 * @return the description
	 */
	public final Object getDescription() {
		return this.description;
	}

	/**
	 * Returns the descriptor's attributes.
	 *
	 * @return the attributes
	 */
	public final Map<String, Object> getAttributes() {
		return this.attributes;
	}

}
