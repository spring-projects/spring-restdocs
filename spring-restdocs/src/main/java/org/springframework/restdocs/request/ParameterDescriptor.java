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

import java.util.HashMap;
import java.util.Map;

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

	ParameterDescriptor(String name) {
		this.name = name;
	}

	/**
	 * Specifies the description of the parameter
	 * 
	 * @param description The parameter's description
	 * @return {@code this}
	 */
	public ParameterDescriptor description(String description) {
		this.description = description;
		return this;
	}

	String getName() {
		return this.name;
	}

	String getDescription() {
		return this.description;
	}

	Map<String, Object> toModel() {
		Map<String, Object> model = new HashMap<>();
		model.put("name", this.name);
		model.put("description", this.description);
		model.putAll(getAttributes());
		return model;
	}

}
