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

package org.springframework.restdocs.state;

/**
 * A description of a field found in a hypermedia API
 * 
 * @see StateDocumentation#fieldWithPath(Path)
 * 
 * @author Andreas Evers
 */
public class FieldDescriptor {

	private final Path path;

	private String type;

	private boolean required;

	private String constraints;

	private String description;

	FieldDescriptor(Path path) {
		this.path = path;
	}

	/**
	 * Specifies the type of the field
	 * 
	 * @param type The field's type (could be number, string, boolean, array, object, ...)
	 * @return {@code this}
	 */
	public FieldDescriptor type(String type) {
		this.type = type;
		return this;
	}

	/**
	 * Specifies necessity of the field
	 * 
	 * @param required The field's necessity
	 * @return {@code this}
	 */
	public FieldDescriptor required(boolean required) {
		this.required = required;
		return this;
	}

	/**
	 * Specifies the constraints of the field
	 * 
	 * @param constraints The field's constraints
	 * @return {@code this}
	 */
	public FieldDescriptor constraints(String constraints) {
		this.constraints = constraints;
		return this;
	}

	/**
	 * Specifies the description of the field
	 * 
	 * @param description The field's description
	 * @return {@code this}
	 */
	public FieldDescriptor description(String description) {
		this.description = description;
		return this;
	}

	Path getPath() {
		return this.path;
	}

	String getType() {
		return this.type;
	}

	boolean isRequired() {
		return this.required;
	}

	String getConstraints() {
		return this.constraints;
	}

	String getDescription() {
		return this.description;
	}
}
