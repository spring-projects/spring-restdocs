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

import java.util.Map;

import org.springframework.restdocs.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.SnippetWritingResultHandler;

/**
 * Static factory methods for documenting aspects of a request sent to a RESTful API.
 * 
 * @author Andy Wilkinson
 */
public abstract class RequestDocumentation {

	private RequestDocumentation() {

	}

	/**
	 * Creates a {@link SnippetWritingResultHandler} that will produce a snippet
	 * documenting a request's query parameters
	 * 
	 * @param identifier An identifier for the API call that is being documented
	 * @param attributes Attributes made available during rendering of the query
	 * parameters snippet
	 * @param descriptors The descriptions of the parameters in the request's query string
	 * @return the result handler
	 * @see RestDocumentationResultHandler#withQueryParameters(ParameterDescriptor...)
	 */
	public static SnippetWritingResultHandler documentQueryParameters(String identifier,
			Map<String, Object> attributes, ParameterDescriptor... descriptors) {
		return new QueryParametersSnippetResultHandler(identifier, attributes,
				descriptors);
	}

	/**
	 * Creates a {@link ParameterDescriptor} that describes a query string parameter with
	 * the given {@code name}.
	 * 
	 * @param name The name of the parameter
	 * @return a {@link ParameterDescriptor} ready for further configuration
	 * @see RestDocumentationResultHandler#withQueryParameters(ParameterDescriptor...)
	 */
	public static ParameterDescriptor parameterWithName(String name) {
		return new ParameterDescriptor(name);
	}

}
