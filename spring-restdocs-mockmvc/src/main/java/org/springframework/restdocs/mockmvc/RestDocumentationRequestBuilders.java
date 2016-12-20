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

package org.springframework.restdocs.mockmvc;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * A drop-in replacement for {@link MockMvcRequestBuilders} that captures a request's URL
 * template and makes it available for documentation. Required when
 * {@link RequestDocumentation#pathParameters(org.springframework.restdocs.request.ParameterDescriptor...)
 * ) documenting path parameters} and recommended for general usage.
 *
 * @author Andy Wilkinson
 * @see MockMvcRequestBuilders
 * @see RequestDocumentation#pathParameters(org.springframework.restdocs.request.ParameterDescriptor...)
 * @see RequestDocumentation#pathParameters(java.util.Map,
 * org.springframework.restdocs.request.ParameterDescriptor...)
 */
public abstract class RestDocumentationRequestBuilders {

	private RestDocumentationRequestBuilders() {

	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a GET request. The url template
	 * will be captured and made available for documentation.
	 *
	 * @param urlTemplate a URL template; the resulting URL will be encoded
	 * @param urlVariables zero or more URL variables
	 * @return the builder for the GET request
	 */
	public static MockHttpServletRequestBuilder get(String urlTemplate,
			Object... urlVariables) {
		return MockMvcRequestBuilders.get(urlTemplate, urlVariables).requestAttr(
				RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, urlTemplate);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a GET request.
	 *
	 * @param uri the URL
	 * @return the builder for the GET request
	 */
	public static MockHttpServletRequestBuilder get(URI uri) {
		return MockMvcRequestBuilders.get(uri);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a POST request. The url template
	 * will be captured and made available for documentation.
	 *
	 * @param urlTemplate a URL template; the resulting URL will be encoded
	 * @param urlVariables zero or more URL variables
	 * @return the builder for the POST request
	 */
	public static MockHttpServletRequestBuilder post(String urlTemplate,
			Object... urlVariables) {
		return MockMvcRequestBuilders.post(urlTemplate, urlVariables).requestAttr(
				RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, urlTemplate);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a POST request.
	 *
	 * @param uri the URL
	 * @return the builder for the POST request
	 */
	public static MockHttpServletRequestBuilder post(URI uri) {
		return MockMvcRequestBuilders.post(uri);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a PUT request. The url template
	 * will be captured and made available for documentation.
	 *
	 * @param urlTemplate a URL template; the resulting URL will be encoded
	 * @param urlVariables zero or more URL variables
	 * @return the builder for the PUT request
	 */
	public static MockHttpServletRequestBuilder put(String urlTemplate,
			Object... urlVariables) {
		return MockMvcRequestBuilders.put(urlTemplate, urlVariables).requestAttr(
				RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, urlTemplate);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a PUT request.
	 *
	 * @param uri the URL
	 * @return the builder for the PUT request
	 */
	public static MockHttpServletRequestBuilder put(URI uri) {
		return MockMvcRequestBuilders.put(uri);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a PATCH request. The url
	 * template will be captured and made available for documentation.
	 *
	 * @param urlTemplate a URL template; the resulting URL will be encoded
	 * @param urlVariables zero or more URL variables
	 * @return the builder for the PATCH request
	 */
	public static MockHttpServletRequestBuilder patch(String urlTemplate,
			Object... urlVariables) {
		return MockMvcRequestBuilders.patch(urlTemplate, urlVariables).requestAttr(
				RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, urlTemplate);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a PATCH request.
	 *
	 * @param uri the URL
	 * @return the builder for the PATCH request
	 */
	public static MockHttpServletRequestBuilder patch(URI uri) {
		return MockMvcRequestBuilders.patch(uri);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a DELETE request. The url
	 * template will be captured and made available for documentation.
	 *
	 * @param urlTemplate a URL template; the resulting URL will be encoded
	 * @param urlVariables zero or more URL variables
	 * @return the builder for the DELETE request
	 */
	public static MockHttpServletRequestBuilder delete(String urlTemplate,
			Object... urlVariables) {
		return MockMvcRequestBuilders.delete(urlTemplate, urlVariables).requestAttr(
				RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, urlTemplate);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a DELETE request.
	 *
	 * @param uri the URL
	 * @return the builder for the DELETE request
	 */
	public static MockHttpServletRequestBuilder delete(URI uri) {
		return MockMvcRequestBuilders.delete(uri);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for an OPTIONS request. The url
	 * template will be captured and made available for documentation.
	 *
	 * @param urlTemplate a URL template; the resulting URL will be encoded
	 * @param urlVariables zero or more URL variables
	 * @return the builder for the OPTIONS request
	 */
	public static MockHttpServletRequestBuilder options(String urlTemplate,
			Object... urlVariables) {
		return MockMvcRequestBuilders.options(urlTemplate, urlVariables).requestAttr(
				RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, urlTemplate);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for an OPTIONS request.
	 *
	 * @param uri the URL
	 * @return the builder for the OPTIONS request
	 */
	public static MockHttpServletRequestBuilder options(URI uri) {
		return MockMvcRequestBuilders.options(uri);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a HEAD request. The url template
	 * will be captured and made available for documentation.
	 *
	 * @param urlTemplate a URL template; the resulting URL will be encoded
	 * @param urlVariables zero or more URL variables
	 * @return the builder for the HEAD request
	 */
	public static MockHttpServletRequestBuilder head(String urlTemplate,
			Object... urlVariables) {
		return MockMvcRequestBuilders.head(urlTemplate, urlVariables).requestAttr(
				RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, urlTemplate);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a HEAD request.
	 *
	 * @param uri the URL
	 * @return the builder for the HEAD request
	 */
	public static MockHttpServletRequestBuilder head(URI uri) {
		return MockMvcRequestBuilders.head(uri);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a request with the given HTTP
	 * method. The url template will be captured and made available for documentation.
	 *
	 * @param httpMethod the HTTP method
	 * @param urlTemplate a URL template; the resulting URL will be encoded
	 * @param urlVariables zero or more URL variables
	 * @return the builder for the request
	 */
	public static MockHttpServletRequestBuilder request(HttpMethod httpMethod,
			String urlTemplate, Object... urlVariables) {
		return MockMvcRequestBuilders.request(httpMethod, urlTemplate, urlVariables)
				.requestAttr(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						urlTemplate);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a request with the given HTTP
	 * method.
	 * @param httpMethod the HTTP method (GET, POST, etc)
	 * @param uri the URL
	 * @return the builder for the request
	 */
	public static MockHttpServletRequestBuilder request(HttpMethod httpMethod, URI uri) {
		return MockMvcRequestBuilders.request(httpMethod, uri);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a multipart request. The url
	 * template will be captured and made available for documentation.
	 *
	 * @param urlTemplate a URL template; the resulting URL will be encoded
	 * @param urlVariables zero or more URL variables
	 * @return the builder for the file upload request
	 */
	public static MockMultipartHttpServletRequestBuilder fileUpload(String urlTemplate,
			Object... urlVariables) {
		return (MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders
				.fileUpload(urlTemplate, urlVariables)
				.requestAttr(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						urlTemplate);
	}

	/**
	 * Create a {@link MockHttpServletRequestBuilder} for a multipart request.
	 *
	 * @param uri the URL
	 * @return the builder for the file upload request
	 */
	public static MockMultipartHttpServletRequestBuilder fileUpload(URI uri) {
		return MockMvcRequestBuilders.fileUpload(uri);
	}

}
