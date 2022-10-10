/*
 * Copyright 2014-2022 the original author or authors.
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

package org.springframework.restdocs.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.operation.OperationRequest;

/**
 * Static factory methods for documenting aspects of a request sent to a RESTful API.
 *
 * @author Andy Wilkinson
 * @author Marcel Overdijk
 */
public abstract class RequestDocumentation {

	private RequestDocumentation() {

	}

	/**
	 * Creates a {@link ParameterDescriptor} that describes a request or path parameter
	 * with the given {@code name}.
	 * @param name the name of the parameter
	 * @return a {@link ParameterDescriptor} ready for further configuration
	 */
	public static ParameterDescriptor parameterWithName(String name) {
		return new ParameterDescriptor(name);
	}

	/**
	 * Creates a {@link RequestPartDescriptor} that describes a request part with the
	 * given {@code name}.
	 * @param name the name of the request part
	 * @return a {@link RequestPartDescriptor} ready for further configuration
	 */
	public static RequestPartDescriptor partWithName(String name) {
		return new RequestPartDescriptor(name);
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
	 * marked as {@link ParameterDescriptor#ignored()}. This will prevent it from
	 * appearing in the generated snippet while avoiding the failure described above.
	 * @param descriptors the descriptions of the parameters in the request's path
	 * @return the snippet that will document the parameters
	 */
	public static PathParametersSnippet pathParameters(ParameterDescriptor... descriptors) {
		return pathParameters(Arrays.asList(descriptors));
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
	 * marked as {@link ParameterDescriptor#ignored()}. This will prevent it from
	 * appearing in the generated snippet while avoiding the failure described above.
	 * @param descriptors the descriptions of the parameters in the request's path
	 * @return the snippet that will document the parameters
	 */
	public static PathParametersSnippet pathParameters(List<ParameterDescriptor> descriptors) {
		return new PathParametersSnippet(descriptors);
	}

	/**
	 * Returns a {@code Snippet} that will document the path parameters from the API
	 * operation's request. The parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a parameter is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented parameters will be ignored.
	 * @param descriptors the descriptions of the parameters in the request's path
	 * @return the snippet that will document the parameters
	 */
	public static PathParametersSnippet relaxedPathParameters(ParameterDescriptor... descriptors) {
		return relaxedPathParameters(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the path parameters from the API
	 * operation's request. The parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a parameter is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented parameters will be ignored.
	 * @param descriptors the descriptions of the parameters in the request's path
	 * @return the snippet that will document the parameters
	 */
	public static PathParametersSnippet relaxedPathParameters(List<ParameterDescriptor> descriptors) {
		return new PathParametersSnippet(descriptors, true);
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
	 * marked as {@link ParameterDescriptor#ignored()}. This will prevent it from
	 * appearing in the generated snippet while avoiding the failure described above.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the parameters in the request's path
	 * @return the snippet that will document the parameters
	 */
	public static PathParametersSnippet pathParameters(Map<String, Object> attributes,
			ParameterDescriptor... descriptors) {
		return pathParameters(attributes, Arrays.asList(descriptors));
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
	 * marked as {@link ParameterDescriptor#ignored()}. This will prevent it from
	 * appearing in the generated snippet while avoiding the failure described above.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the parameters in the request's path
	 * @return the snippet that will document the parameters
	 */
	public static PathParametersSnippet pathParameters(Map<String, Object> attributes,
			List<ParameterDescriptor> descriptors) {
		return new PathParametersSnippet(descriptors, attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the path parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the parameters will be documented using the given {@code descriptors}
	 * .
	 * <p>
	 * If a parameter is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented parameters will be ignored.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the parameters in the request's path
	 * @return the snippet that will document the parameters
	 */
	public static PathParametersSnippet relaxedPathParameters(Map<String, Object> attributes,
			ParameterDescriptor... descriptors) {
		return relaxedPathParameters(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the path parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the parameters will be documented using the given {@code descriptors}
	 * .
	 * <p>
	 * If a parameter is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented parameters will be ignored.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the parameters in the request's path
	 * @return the snippet that will document the parameters
	 */
	public static PathParametersSnippet relaxedPathParameters(Map<String, Object> attributes,
			List<ParameterDescriptor> descriptors) {
		return new PathParametersSnippet(descriptors, attributes, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the query parameters from the API
	 * operation's request. The query parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a query parameter is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * query parameter is documented, is not marked as optional, and is not present in the
	 * request, a failure will also occur.
	 * <p>
	 * If you do not want to document a query parameter, a parameter descriptor can be
	 * marked as {@link ParameterDescriptor#ignored()}. This will prevent it from
	 * appearing in the generated snippet while avoiding the failure described above.
	 * @param descriptors the descriptions of the request's query parameters
	 * @return the snippet
	 * @since 3.0.0
	 */
	public static QueryParametersSnippet queryParameters(ParameterDescriptor... descriptors) {
		return queryParameters(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the query parameters from the API
	 * operation's request. The query parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a query parameter is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * query parameter is documented, is not marked as optional, and is not present in the
	 * request, a failure will also occur.
	 * <p>
	 * If you do not want to document a query parameter, a parameter descriptor can be
	 * marked as {@link ParameterDescriptor#ignored()}. This will prevent it from
	 * appearing in the generated snippet while avoiding the failure described above.
	 * @param descriptors the descriptions of the request's query parameters
	 * @return the snippet
	 * @since 3.0.0
	 */
	public static QueryParametersSnippet queryParameters(List<ParameterDescriptor> descriptors) {
		return new QueryParametersSnippet(descriptors);
	}

	/**
	 * Returns a {@code Snippet} that will document the query parameters from the API
	 * operation's request. The query parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a query parameter is documented, is not marked as optional, and is not present
	 * in the response, a failure will occur. Any undocumented query parameters will be
	 * ignored.
	 * @param descriptors the descriptions of the request's query parameters
	 * @return the snippet
	 * @since 3.0.0
	 */
	public static QueryParametersSnippet relaxedQueryParameters(ParameterDescriptor... descriptors) {
		return relaxedQueryParameters(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the query parameters from the API
	 * operation's request. The query parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a parameter is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented query parameters will be ignored.
	 * @param descriptors the descriptions of the request's query parameters
	 * @return the snippet
	 * @since 3.0.0
	 */
	public static QueryParametersSnippet relaxedQueryParameters(List<ParameterDescriptor> descriptors) {
		return new QueryParametersSnippet(descriptors, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the query parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the query parameters will be documented using the given
	 * {@code descriptors} .
	 * <p>
	 * If a query parameter is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * query parameter is documented, is not marked as optional, and is not present in the
	 * request, a failure will also occur.
	 * <p>
	 * If you do not want to document a query parameter, a parameter descriptor can be
	 * marked as {@link ParameterDescriptor#ignored()}. This will prevent it from
	 * appearing in the generated snippet while avoiding the failure described above.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's query parameters
	 * @return the snippet that will document the query parameters
	 * @since 3.0.0
	 */
	public static QueryParametersSnippet queryParameters(Map<String, Object> attributes,
			ParameterDescriptor... descriptors) {
		return queryParameters(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the query parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the query parameters will be documented using the given
	 * {@code descriptors} .
	 * <p>
	 * If a query parameter is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a
	 * query parameter is documented, is not marked as optional, and is not present in the
	 * request, a failure will also occur.
	 * <p>
	 * If you do not want to document a query parameter, a parameter descriptor can be
	 * marked as {@link ParameterDescriptor#ignored()}. This will prevent it from
	 * appearing in the generated snippet while avoiding the failure described above.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's query parameters
	 * @return the snippet that will document the query parameters
	 * @since 3.0.0
	 */
	public static QueryParametersSnippet queryParameters(Map<String, Object> attributes,
			List<ParameterDescriptor> descriptors) {
		return new QueryParametersSnippet(descriptors, attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the query parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the query parameters will be documented using the given
	 * {@code descriptors} .
	 * <p>
	 * If a query parameter is documented, is not marked as optional, and is not present
	 * in the response, a failure will occur. Any undocumented query parameters will be
	 * ignored.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's query parameters
	 * @return the snippet that will document the query parameters
	 * @since 3.0.0
	 */
	public static QueryParametersSnippet relaxedQueryParameters(Map<String, Object> attributes,
			ParameterDescriptor... descriptors) {
		return relaxedQueryParameters(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the query parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the query parameters will be documented using the given
	 * {@code descriptors} .
	 * <p>
	 * If a query parameter is documented, is not marked as optional, and is not present
	 * in the response, a failure will occur. Any undocumented query parameters will be
	 * ignored.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's query parameters
	 * @return the snippet that will document the query parameters
	 * @since 3.0.0
	 */
	public static QueryParametersSnippet relaxedQueryParameters(Map<String, Object> attributes,
			List<ParameterDescriptor> descriptors) {
		return new QueryParametersSnippet(descriptors, attributes, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the form parameters from the API
	 * operation's request. The form parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a form parameter is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a form
	 * parameter is documented, is not marked as optional, and is not present in the
	 * request, a failure will also occur.
	 * <p>
	 * If you do not want to document a form parameter, a parameter descriptor can be
	 * marked as {@link ParameterDescriptor#ignored()}. This will prevent it from
	 * appearing in the generated snippet while avoiding the failure described above.
	 * @param descriptors the descriptions of the request's form parameters
	 * @return the snippet
	 * @since 3.0.0
	 */
	public static FormParametersSnippet formParameters(ParameterDescriptor... descriptors) {
		return formParameters(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the form parameters from the API
	 * operation's request. The form parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a form parameter is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a form
	 * parameter is documented, is not marked as optional, and is not present in the
	 * request, a failure will also occur.
	 * <p>
	 * If you do not want to document a form parameter, a parameter descriptor can be
	 * marked as {@link ParameterDescriptor#ignored()}. This will prevent it from
	 * appearing in the generated snippet while avoiding the failure described above.
	 * @param descriptors the descriptions of the request's form parameters
	 * @return the snippet
	 * @since 3.0.0
	 */
	public static FormParametersSnippet formParameters(List<ParameterDescriptor> descriptors) {
		return new FormParametersSnippet(descriptors);
	}

	/**
	 * Returns a {@code Snippet} that will document the form parameters from the API
	 * operation's request. The form parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a form parameter is documented, is not marked as optional, and is not present in
	 * the response, a failure will occur. Any undocumented form parameters will be
	 * ignored.
	 * @param descriptors the descriptions of the request's form parameters
	 * @return the snippet
	 * @since 3.0.0
	 */
	public static FormParametersSnippet relaxedFormParameters(ParameterDescriptor... descriptors) {
		return relaxedFormParameters(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the form parameters from the API
	 * operation's request. The form parameters will be documented using the given
	 * {@code descriptors}.
	 * <p>
	 * If a parameter is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented form parameters will be ignored.
	 * @param descriptors the descriptions of the request's form parameters
	 * @return the snippet
	 * @since 3.0.0
	 */
	public static FormParametersSnippet relaxedFormParameters(List<ParameterDescriptor> descriptors) {
		return new FormParametersSnippet(descriptors, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the form parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the form parameters will be documented using the given
	 * {@code descriptors} .
	 * <p>
	 * If a form parameter is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a form
	 * parameter is documented, is not marked as optional, and is not present in the
	 * request, a failure will also occur.
	 * <p>
	 * If you do not want to document a form parameter, a parameter descriptor can be
	 * marked as {@link ParameterDescriptor#ignored()}. This will prevent it from
	 * appearing in the generated snippet while avoiding the failure described above.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's form parameters
	 * @return the snippet that will document the form parameters
	 * @since 3.0.0
	 */
	public static FormParametersSnippet formParameters(Map<String, Object> attributes,
			ParameterDescriptor... descriptors) {
		return formParameters(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the form parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the form parameters will be documented using the given
	 * {@code descriptors} .
	 * <p>
	 * If a form parameter is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a form
	 * parameter is documented, is not marked as optional, and is not present in the
	 * request, a failure will also occur.
	 * <p>
	 * If you do not want to document a form parameter, a parameter descriptor can be
	 * marked as {@link ParameterDescriptor#ignored()}. This will prevent it from
	 * appearing in the generated snippet while avoiding the failure described above.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's form parameters
	 * @return the snippet that will document the form parameters
	 * @since 3.0.0
	 */
	public static FormParametersSnippet formParameters(Map<String, Object> attributes,
			List<ParameterDescriptor> descriptors) {
		return new FormParametersSnippet(descriptors, attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the form parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the form parameters will be documented using the given
	 * {@code descriptors} .
	 * <p>
	 * If a form parameter is documented, is not marked as optional, and is not present in
	 * the response, a failure will occur. Any undocumented form parameters will be
	 * ignored.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's form parameters
	 * @return the snippet that will document the form parameters
	 * @since 3.0.0
	 */
	public static FormParametersSnippet relaxedFormParameters(Map<String, Object> attributes,
			ParameterDescriptor... descriptors) {
		return relaxedFormParameters(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the form parameters from the API
	 * operation's request. The given {@code attributes} will be available during snippet
	 * rendering and the form parameters will be documented using the given
	 * {@code descriptors} .
	 * <p>
	 * If a form parameter is documented, is not marked as optional, and is not present in
	 * the response, a failure will occur. Any undocumented form parameters will be
	 * ignored.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's form parameters
	 * @return the snippet that will document the form parameters
	 * @since 3.0.0
	 */
	public static FormParametersSnippet relaxedFormParameters(Map<String, Object> attributes,
			List<ParameterDescriptor> descriptors) {
		return new FormParametersSnippet(descriptors, attributes, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the parts from the API operation's
	 * request. The parts will be documented using the given {@code descriptors}.
	 * <p>
	 * If a part is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a part
	 * is documented, is not marked as optional, and is not present in the request, a
	 * failure will also occur.
	 * <p>
	 * If you do not want to document a part, a part descriptor can be marked as
	 * {@link RequestPartDescriptor#ignored()}. This will prevent it from appearing in the
	 * generated snippet while avoiding the failure described above.
	 * @param descriptors the descriptions of the request's parts
	 * @return the snippet
	 * @see OperationRequest#getParts()
	 */
	public static RequestPartsSnippet requestParts(RequestPartDescriptor... descriptors) {
		return requestParts(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the parts from the API operation's
	 * request. The parts will be documented using the given {@code descriptors}.
	 * <p>
	 * If a part is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a part
	 * is documented, is not marked as optional, and is not present in the request, a
	 * failure will also occur.
	 * <p>
	 * If you do not want to document a part, a part descriptor can be marked as
	 * {@link RequestPartDescriptor#ignored()}. This will prevent it from appearing in the
	 * generated snippet while avoiding the failure described above.
	 * @param descriptors the descriptions of the request's parts
	 * @return the snippet
	 * @see OperationRequest#getParts()
	 */
	public static RequestPartsSnippet requestParts(List<RequestPartDescriptor> descriptors) {
		return new RequestPartsSnippet(descriptors);
	}

	/**
	 * Returns a {@code Snippet} that will document the parts from the API operation's
	 * request. The parameters will be documented using the given {@code descriptors}.
	 * <p>
	 * If a part is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented parts will be ignored.
	 * @param descriptors the descriptions of the request's parts
	 * @return the snippet
	 * @see OperationRequest#getParts()
	 */
	public static RequestPartsSnippet relaxedRequestParts(RequestPartDescriptor... descriptors) {
		return relaxedRequestParts(Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the parts from the API operation's
	 * request. The parameters will be documented using the given {@code descriptors}.
	 * <p>
	 * If a part is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented parts will be ignored.
	 * @param descriptors the descriptions of the request's parts
	 * @return the snippet
	 * @see OperationRequest#getParts()
	 */
	public static RequestPartsSnippet relaxedRequestParts(List<RequestPartDescriptor> descriptors) {
		return new RequestPartsSnippet(descriptors, true);
	}

	/**
	 * Returns a {@code Snippet} that will document the parts from the API operation's
	 * request. The given {@code attributes} will be available during snippet rendering
	 * and the parts will be documented using the given {@code descriptors}.
	 * <p>
	 * If a part is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a part
	 * is documented, is not marked as optional, and is not present in the request, a
	 * failure will also occur.
	 * <p>
	 * If you do not want to document a part, a part descriptor can be marked as
	 * {@link RequestPartDescriptor#ignored()}. This will prevent it from appearing in the
	 * generated snippet while avoiding the failure described above.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's parts
	 * @return the snippet
	 * @see OperationRequest#getParts()
	 */
	public static RequestPartsSnippet requestParts(Map<String, Object> attributes,
			RequestPartDescriptor... descriptors) {
		return requestParts(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the parts from the API operation's
	 * request. The given {@code attributes} will be available during snippet rendering
	 * and the parts will be documented using the given {@code descriptors}.
	 * <p>
	 * If a part is present in the request, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a part
	 * is documented, is not marked as optional, and is not present in the request, a
	 * failure will also occur.
	 * <p>
	 * If you do not want to document a part, a part descriptor can be marked as
	 * {@link RequestPartDescriptor#ignored()}. This will prevent it from appearing in the
	 * generated snippet while avoiding the failure described above.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's parts
	 * @return the snippet
	 * @see OperationRequest#getParts()
	 */
	public static RequestPartsSnippet requestParts(Map<String, Object> attributes,
			List<RequestPartDescriptor> descriptors) {
		return new RequestPartsSnippet(descriptors, attributes);
	}

	/**
	 * Returns a {@code Snippet} that will document the parts from the API operation's
	 * request. The given {@code attributes} will be available during snippet rendering
	 * and the parts will be documented using the given {@code descriptors}.
	 * <p>
	 * If a part is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented parts will be ignored.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's parts
	 * @return the snippet
	 */
	public static RequestPartsSnippet relaxedRequestParts(Map<String, Object> attributes,
			RequestPartDescriptor... descriptors) {
		return relaxedRequestParts(attributes, Arrays.asList(descriptors));
	}

	/**
	 * Returns a {@code Snippet} that will document the parts from the API operation's
	 * request. The given {@code attributes} will be available during snippet rendering
	 * and the parts will be documented using the given {@code descriptors}.
	 * <p>
	 * If a part is documented, is not marked as optional, and is not present in the
	 * request, a failure will occur. Any undocumented parts will be ignored.
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the request's parts
	 * @return the snippet
	 */
	public static RequestPartsSnippet relaxedRequestParts(Map<String, Object> attributes,
			List<RequestPartDescriptor> descriptors) {
		return new RequestPartsSnippet(descriptors, attributes, true);
	}

}
