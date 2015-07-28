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

package org.springframework.restdocs.payload;

import java.util.HashMap;
import java.util.Map;

import org.springframework.restdocs.snippet.AbstractDescriptor;

/**
 * A description of a field found in a request or response payload
 * 
 * @see PayloadDocumentation#fieldWithPath(String)
 * 
 * @author Andreas Evers
 * @author Andy Wilkinson
 */
public class FieldDescriptor extends AbstractDescriptor<FieldDescriptor> {

	private final String path;

	private FieldType type;

	private boolean optional;

	private String description;

	FieldDescriptor(String path) {
		this.path = path;
	}

	/**
	 * Specifies the type of the field
	 * 
	 * @param type The type of the field
	 * 
	 * @return {@code this}
	 */
	public FieldDescriptor type(FieldType type) {
		this.type = type;
		return this;
	}

	/**
	 * Marks the field as optional
	 * 
	 * @return {@code this}
	 */
	public FieldDescriptor optional() {
		this.optional = true;
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

	String getPath() {
		return this.path;
	}

	FieldType getType() {
		return this.type;
	}

	boolean isOptional() {
		return this.optional;
	}

	String getDescription() {
		return this.description;
	}

	Map<String, Object> toModel() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("path", this.path);
		model.put("type", this.type.toString());
		model.put("description", this.description);
		model.put("optional", this.optional);
		model.putAll(this.getAttributes());
		return model;
	}
}
