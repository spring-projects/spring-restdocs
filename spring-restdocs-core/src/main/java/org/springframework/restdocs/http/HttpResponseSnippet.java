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

package org.springframework.restdocs.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.TemplatedSnippet;

/**
 * A {@link Snippet} that documents an HTTP response.
 *
 * @author Andy Wilkinson
 * @see HttpDocumentation#httpResponse()
 * @see HttpDocumentation#httpResponse(Map)
 */
public class HttpResponseSnippet extends TemplatedSnippet {

	/**
	 * Creates a new {@code HttpResponseSnippet} with no additional attributes.
	 */
	protected HttpResponseSnippet() {
		this(null);
	}

	/**
	 * Creates a new {@code HttpResponseSnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 *
	 * @param attributes The additional attributes
	 */
	protected HttpResponseSnippet(Map<String, Object> attributes) {
		super("http-response", attributes);
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		OperationResponse response = operation.getResponse();
		HttpStatus status = response.getStatus();
		Map<String, Object> model = new HashMap<>();
		model.put("responseBody", responseBody(response));
		model.put("statusCode", status.value());
		model.put("statusReason", status.getReasonPhrase());
		model.put("headers", headers(response));
		return model;
	}

	private String responseBody(OperationResponse response) {
		String content = response.getContentAsString();
		return content.isEmpty() ? content : String.format("%n%s", content);
	}

	private List<Map<String, String>> headers(OperationResponse response) {
		List<Map<String, String>> headers = new ArrayList<>();
		for (Entry<String, List<String>> header : response.getHeaders().entrySet()) {
			List<String> values = header.getValue();
			for (String value : values) {
				headers.add(header(header.getKey(), value));
			}
		}
		return headers;
	}

	private Map<String, String> header(String name, String value) {
		Map<String, String> header = new HashMap<>();
		header.put("name", name);
		header.put("value", value);
		return header;
	}

}
