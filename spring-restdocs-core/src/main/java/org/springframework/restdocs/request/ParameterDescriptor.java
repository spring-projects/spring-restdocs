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

package org.springframework.restdocs.request;

import org.springframework.restdocs.snippet.AbstractDescriptor;

/**
 * A descriptor of a request or path parameter
 * 
 * @author Andy Wilkinson
 * @see RequestDocumentation#parameterWithName
 *
 */
public class ParameterDescriptor extends AbstractDescriptor<ParameterDescriptor> {

	private final String name;

	private String description;

	/**
	 * Creates a new {@code ParameterDescriptor} describing the parameter with the given
	 * {@code name}.
	 * 
	 * @param name the name of the parameter
	 */
	protected ParameterDescriptor(String name) {
		this.name = name;
	}

	/**
	 * Specifies the description of the parameter
	 * 
	 * @param description The parameter's description
	 * @return {@code this}
	 */
	public final ParameterDescriptor description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Returns the name of the parameter being described by this descriptor
	 * 
	 * @return the name of the parameter
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Returns the description of the parameter
	 * 
	 * @return the description
	 */
	public final String getDescription() {
		return this.description;
	}

}
