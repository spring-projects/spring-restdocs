/*
 * Copyright 2014-2017 the original author or authors.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.RequestCookie;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.Assert;

/**
 * A {@link Snippet} that documents the request URI supported by a RESTful resource.
 *
 * @author Ryan O'Meara
 * @see RequestDocumentation#requestUri()
 * @see RequestDocumentation#requestUri(Map)
 */
public class RequestUriSnippet extends TemplatedSnippet {

	private static final String MULTIPART_BOUNDARY = "6o2knFse3p53ty9dmcQvWAIx1zInP11uCfbm";

	/**
	 * Creates a new {@code RequestUriSnippet} with no additional attributes.
	 */
	protected RequestUriSnippet() {
		this(null);
	}

	/**
	 * Creates a new {@code RequestUriSnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 *
	 * @param attributes The additional attributes
	 */
	protected RequestUriSnippet(Map<String, Object> attributes) {
		super("request-uri", attributes);
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		Map<String, Object> model = new HashMap<>();
		model.put("method", operation.getRequest().getMethod());
		model.put("urlTemplate", getUrlTemplate(operation));
		model.put("headers", getHeaders(operation.getRequest()));
		return model;
	}

	private boolean includeParametersInUri(OperationRequest request) {
		return request.getMethod() == HttpMethod.GET || request.getContent().length > 0;
	}

	private String getUrlTemplate(Operation operation) {
		String urlTemplate = (String) operation.getAttributes()
				.get(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE);
		Assert.notNull(urlTemplate, "urlTemplate not found. If you are using MockMvc did "
				+ "you use RestDocumentationRequestBuilders to build the request?");
		return removeQueryStringIfPresent(urlTemplate);
	}

	private List<Map<String, String>> getHeaders(OperationRequest request) {
		List<Map<String, String>> headers = new ArrayList<>();

		for (Entry<String, List<String>> header : request.getHeaders().entrySet()) {
			if (HttpHeaders.ACCEPT.equals(header.getKey())
					|| HttpHeaders.CONTENT_TYPE.equals(header.getKey())) {
				for (String value : header.getValue()) {
					if (HttpHeaders.CONTENT_TYPE.equals(header.getKey())
							&& !request.getParts().isEmpty()) {
						headers.add(header(header.getKey(), String
								.format("%s; boundary=%s", value, MULTIPART_BOUNDARY)));
					}
					else {
						headers.add(header(header.getKey(), value));
					}

				}
			}
		}

		for (RequestCookie cookie : request.getCookies()) {
			headers.add(header(HttpHeaders.COOKIE,
					String.format("%s=%s", cookie.getName(), cookie.getValue())));
		}

		if (requiresFormEncodingContentTypeHeader(request)) {
			headers.add(header(HttpHeaders.CONTENT_TYPE,
					MediaType.APPLICATION_FORM_URLENCODED_VALUE));
		}
		return headers;
	}

	private boolean isPutOrPost(OperationRequest request) {
		return HttpMethod.PUT.equals(request.getMethod())
				|| HttpMethod.POST.equals(request.getMethod());
	}

	private boolean requiresFormEncodingContentTypeHeader(OperationRequest request) {
		return request.getHeaders().get(HttpHeaders.CONTENT_TYPE) == null
				&& isPutOrPost(request) && (!request.getParameters().isEmpty()
						&& !includeParametersInUri(request));
	}

	private Map<String, String> header(String name, String value) {
		Map<String, String> header = new HashMap<>();
		header.put("name", name);
		header.put("value", value);
		return header;
	}

	private String removeQueryStringIfPresent(String urlTemplate) {
		int index = urlTemplate.indexOf('?');
		if (index == -1) {
			return urlTemplate;
		}
		return urlTemplate.substring(0, index);
	}

}
