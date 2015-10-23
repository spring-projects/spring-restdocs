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

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.Snippet;

import java.util.List;
import java.util.Map;

/**
 * A {@link Snippet} that documents the path parameters supported by a RESTful resource.
 * This class may be usefull when the path with parameters cannot be retrieved from
 * the request.
 * As of today it's only usefull when rest-assured is used.
 *
 * @author Yann Le Guern
 * @see PathParametersSnippet
 */
class CustomPathParametersSnippet extends PathParametersSnippet {

	private String pathWithParameters;

	CustomPathParametersSnippet(String pathWithParameters, List<ParameterDescriptor> descriptors) {
		this(pathWithParameters, null, descriptors);
	}

	CustomPathParametersSnippet(String pathWithParameters, Map<String, Object> attributes,
									   List<ParameterDescriptor> descriptors) {
		super(attributes, descriptors);
		this.pathWithParameters = pathWithParameters;
	}

	@Override
	protected String extractUrlTemplate(Operation operation) {
		return pathWithParameters;
	}
}
