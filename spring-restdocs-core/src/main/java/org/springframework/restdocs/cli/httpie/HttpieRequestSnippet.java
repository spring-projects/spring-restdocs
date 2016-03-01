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

package org.springframework.restdocs.cli.httpie;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.cli.AbstractCliSnippet;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.util.StringUtils;

/**
 * A {@link Snippet} that documents the httpie command for a request.
 *
 * @author Raman Gupta
 * @see HttpieDocumentation#httpieRequest()
 * @see HttpieDocumentation#httpieRequest(Map)
 */
public class HttpieRequestSnippet extends AbstractCliSnippet {

	/**
	 * Creates a new {@code CurlRequestSnippet} with no additional attributes.
	 */
	protected HttpieRequestSnippet() {
		this(null);
	}

	/**
	 * Creates a new {@code CurlRequestSnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 *
	 * @param attributes The additional attributes
	 */
	protected HttpieRequestSnippet(Map<String, Object> attributes) {
		super("httpie-request", attributes);
	}

	@Override
	protected Map<String, Object> createModel(final Operation operation) {
		Map<String, Object> model = new HashMap<>();
		model.put("echo_content", getContentStdIn(operation));
		model.put("options", getOptions(operation));
		model.put("url", getUrl(operation));
		model.put("request_items", getRequestItems(operation));
		return model;
	}
	private Object getContentStdIn(final Operation operation) {
		OperationRequest request = operation.getRequest();
		String content = request.getContentAsString();
		if (StringUtils.hasText(content)) {
			return String.format("echo '%s' | ", content);
		}
		else {
			return "";
		}
	}

	private String getOptions(Operation operation) {
		StringWriter options = new StringWriter();
		PrintWriter printer = new PrintWriter(options);
		writeOptions(operation.getRequest(), printer);
		writeUserOptionIfNecessary(operation.getRequest(), printer);
		writeMethodIfNecessary(operation.getRequest(), printer);
		return options.toString();
	}

	private String getUrl(Operation operation) {
		return String.format("'%s'", operation.getRequest().getUri());
	}

	private String getRequestItems(final Operation operation) {
		StringWriter requestItems = new StringWriter();
		PrintWriter printer = new PrintWriter(requestItems);
		writeFormDataIfNecessary(operation.getRequest(), printer);
		writeHeaders(operation.getRequest(), printer);
		writeContent(operation.getRequest(), printer);
		return requestItems.toString();
	}
	private void writeOptions(OperationRequest request, PrintWriter writer) {
		Parameters uniqueParameters = getUniqueParameters(request);
		if (request.getParts().size() > 0 || uniqueParameters.size() > 0) {
			writer.print("--form ");
		}
	}

	private void writeUserOptionIfNecessary(OperationRequest request,
			PrintWriter writer) {
		List<String> headerValue = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
		if (isBasicAuthHeader(headerValue)) {
			String credentials = decodeBasicAuthHeader(headerValue);
			writer.print(String.format("--auth '%s' ", credentials));
		}
	}

	private void writeMethodIfNecessary(OperationRequest request, PrintWriter writer) {
		writer.print(String.format("%s", request.getMethod().name()));
	}

	private void writeFormDataIfNecessary(OperationRequest request, PrintWriter writer) {
		for (OperationRequestPart part : request.getParts()) {
			writer.printf(" \\\n  '%s'", part.getName());
			if (!StringUtils.hasText(part.getSubmittedFileName())) {
				// httpie https://github.com/jkbrzt/httpie/issues/342
				writer.printf("@<(echo '%s')", part.getContentAsString());
			}
			else {
				writer.printf("@'%s'", part.getSubmittedFileName());
			}
			// httpie does not currently support manually set content type by part
		}
	}

	private void writeHeaders(OperationRequest request, PrintWriter writer) {
		HttpHeaders headers = request.getHeaders();
		for (Entry<String, List<String>> entry : headers.entrySet()) {
			if (allowedHeader(entry)) {
				for (String header : entry.getValue()) {
					// form Content-Type not required, added automatically by httpie with --form
					if (request.getParts().size() > 0
							&& entry.getKey().equals(HttpHeaders.CONTENT_TYPE)
							&& header.startsWith("multipart/form-data")) {
						continue;
					}
					writer.print(String.format(" '%s:%s'", entry.getKey(), header));
				}
			}
		}
	}

	private void writeContent(OperationRequest request, PrintWriter writer) {
		String content = request.getContentAsString();
		if (!StringUtils.hasText(content)) {
			if (!request.getParts().isEmpty()) {
				for (Entry<String, List<String>> entry : request.getParameters().entrySet()) {
					for (String value : entry.getValue()) {
						writer.print(String.format(" '%s=%s'", entry.getKey(), value));
					}
				}
			}
			else if (isPutOrPost(request)) {
				writeContentUsingParameters(request, writer);
			}
		}
	}

	private void writeContentUsingParameters(OperationRequest request,
			PrintWriter writer) {
		Parameters uniqueParameters = getUniqueParameters(request);
		for (Map.Entry<String, List<String>> entry : uniqueParameters.entrySet()) {
			if (entry.getValue().isEmpty()) {
				writer.append(String.format(" '%s='", entry.getKey()));
			}
			else {
				for (String value : entry.getValue()) {
					writer.append(String.format(" '%s=%s'", entry.getKey(), value));
				}
			}
		}
	}
}
