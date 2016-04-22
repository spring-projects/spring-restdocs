/*
 * Copyright 2014-2016 the original author or authors.
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

import org.springframework.restdocs.operation.OperationRequest;

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
	 * Returns a {@code Snippet} that will document the path parameters from the API
	 * operation's request. The parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a parameter is present in the request path, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * parameter is documented, is not marked as optional, and is not present in the
	 * request path, a failure will also occur.
	 * <p>
	 * If you do not want to document a path parameter, a parameter descriptor can be
	 * marked as {@link ParameterDescriptor#ignored}. This will prevent it from appearing
	 * in the generated snippet while avoiding the failure described above.
	 *
	 * @param descriptors the descriptions of the parameters in the request's path
	 * @return the snippet that will document the parameters
	 */
	public static PathParametersSnippet pathParameters(
			ParameterDescriptor... descriptors) {
		return new PathParametersSnippet(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the path parameters from the API
	 * operation's request. The parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a parameter is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented parameters will be ignored.
	 *
	 * @param descriptors the descriptions of the parameters in the request's path
	 * @return the snippet that will document the parameters
	 */
	public static PathParametersSnippet relaxedPathParameters(
			ParameterDescriptor... descriptors) {
		return new PathParametersSnippet(Arrays.asList(descriptors), true);
	}

	/**
	 * Returns a {@code Snippet} that will document the path parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the parameters will be documented using the given {@code descriptors}
	 * .
	 * <p>
	 * If a parameter is present in the request path, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * parameter is documented, is not marked as optional, and is not present in the
	 * request path, a failure will also occur.
	 * <p>
	 * If you do not want to document a path parameter, a parameter descriptor can be
	 * marked as {@link ParameterDescriptor#ignored}. This will prevent it from appearing
	 * in the generated snippet while avoiding the failure described above.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the parameters in the request's path
	 * @return the snippet that will document the parameters
	 */
	public static PathParametersSnippet pathParameters(Map<String, Object> attributes,
			ParameterDescriptor... descriptors) {
		return new PathParametersSnippet(Arrays.asList(descriptors), attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the path parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the parameters will be documented using the given {@code descriptors}
	 * .
	 * <p>
	 * If a parameter is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented parameters will be ignored.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the parameters in the request's path
	 * @return the snippet that will document the parameters
	 */
	public static PathParametersSnippet relaxedPathParameters(
			Map<String, Object> attributes, ParameterDescriptor... descriptors) {
		return new PathParametersSnippet(Arrays.asList(descriptors), attributes, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the parameters from the API
	 * operation's request. The parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a parameter is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * parameter is documented, is not marked as optional, and is not present in the
	 * request, a failure will also occur.
	 * <p>
	 * If you do not want to document a request parameter, a parameter descriptor can be
	 * marked as {@link ParameterDescriptor#ignored}. This will prevent it from appearing
	 * in the generated snippet while avoiding the failure described above.
	 *
	 * @param descriptors The descriptions of the request's parameters
	 * @return the snippet
	 * @see OperationRequest#getParameters()
	 */
	public static RequestParametersSnippet requestParameters(
			ParameterDescriptor... descriptors) {
		return new RequestParametersSnippet(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the parameters from the API
	 * operation's request. The parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a parameter is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented parameters will be ignored.
	 *
	 * @param descriptors The descriptions of the request's parameters
	 * @return the snippet
	 * @see OperationRequest#getParameters()
	 */
	public static RequestParametersSnippet relaxedRequestParameters(
			ParameterDescriptor... descriptors) {
		return new RequestParametersSnippet(Arrays.asList(descriptors), true);
	}

	/**
	 * Returns a {@code Snippet} that will document the parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the parameters will be documented using the given {@code descriptors}
	 * .
	 * <p>
	 * If a parameter is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * parameter is documented, is not marked as optional, and is not present in the
	 * request, a failure will also occur.
	 * <p>
	 * If you do not want to document a request parameter, a parameter descriptor can be
	 * marked as {@link ParameterDescriptor#ignored}. This will prevent it from appearing
	 * in the generated snippet while avoiding the failure described above.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's parameters
	 * @return the snippet that will document the parameters
	 * @see OperationRequest#getParameters()
	 */
	public static RequestParametersSnippet requestParameters(
			Map<String, Object> attributes, ParameterDescriptor... descriptors) {
		return new RequestParametersSnippet(Arrays.asList(descriptors), attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the parameters will be documented using the given {@code descriptors}
	 * .
	 * <p>
	 * If a parameter is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented parameters will be ignored.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's parameters
	 * @return the snippet that will document the parameters
	 * @see OperationRequest#getParameters()
	 */
	public static RequestParametersSnippet relaxedRequestParameters(
			Map<String, Object> attributes, ParameterDescriptor... descriptors) {
		return new RequestParametersSnippet(Arrays.asList(descriptors), attributes, true);
	}

}
