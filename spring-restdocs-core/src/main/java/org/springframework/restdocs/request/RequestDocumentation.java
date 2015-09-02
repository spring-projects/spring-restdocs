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

import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.springframework.restdocs.snippet.Snippet;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Static factory methods for documenting aspects of a request sent to a RESTful API.
 * 
 * @author Andy Wilkinson
 */
public abstract class RequestDocumentation {

	private RequestDocumentation() {

	}

	/**
	 * Creates a {@link ParameterDescriptor} that describes a request or path parameter
	 * with the given {@code name}.
	 * 
	 * @param name The name of the parameter
	 * @return a {@link ParameterDescriptor} ready for further configuration
	 */
	public static ParameterDescriptor parameterWithName(String name) {
		return new ParameterDescriptor(name);
	}

	/**
	 * Returns a snippet that will document the path parameters from the API call's
	 * request.
	 * 
	 * @param descriptors The descriptions of the parameters in the request's path
	 * @return the snippet
	 * @see PathVariable
	 */
	public static Snippet pathParameters(ParameterDescriptor... descriptors) {
		return new PathParametersSnippet(Arrays.asList(descriptors));
	}

	/**
	 * Returns a snippet that will document the path parameters from the API call's
	 * request. The given {@code attributes} will be available during snippet rendering.
	 * 
	 * @param attributes Attributes made available during rendering of the path parameters
	 * snippet
	 * @param descriptors The descriptions of the parameters in the request's path
	 * @return the snippet
	 * @see PathVariable
	 */
	public static Snippet pathParameters(Map<String, Object> attributes,
			ParameterDescriptor... descriptors) {
		return new PathParametersSnippet(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a snippet that will document the request parameters from the API call's
	 * request.
	 * 
	 * @param descriptors The descriptions of the request's parameters
	 * @return the snippet
	 * @see RequestParam
	 * @see ServletRequest#getParameterMap()
	 */
	public static Snippet requestParameters(ParameterDescriptor... descriptors) {
		return new RequestParametersSnippet(Arrays.asList(descriptors));
	}

	/**
	 * Returns a snippet that will document the request parameters from the API call's
	 * request. The given {@code attributes} will be available during snippet rendering.
	 * 
	 * @param attributes Attributes made available during rendering of the request
	 * parameters snippet
	 * @param descriptors The descriptions of the request's parameters
	 * @return the snippet
	 * @see RequestParam
	 * @see ServletRequest#getParameterMap()
	 */
	public static Snippet requestParameters(Map<String, Object> attributes,
			ParameterDescriptor... descriptors) {
		return new RequestParametersSnippet(attributes, Arrays.asList(descriptors));
	}

}
