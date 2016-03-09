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

package org.springframework.restdocs.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.StringUtils;

/**
 * A {@link Snippet} that documents the HTTPie command for a request.
 *
 * @author Raman Gupta
 * @author Andy Wilkinson
 * @see CliDocumentation#httpieRequest()
 * @see CliDocumentation#httpieRequest(Map)
 */
public class HttpieRequestSnippet extends TemplatedSnippet {

	/**
	 * Creates a new {@code HttpieRequestSnippet} with no additional attributes.
	 */
	protected HttpieRequestSnippet() {
		this(null);
	}

	/**
	 * Creates a new {@code HttpieRequestSnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 *
	 * @param attributes The additional attributes
	 */
	protected HttpieRequestSnippet(Map<String, Object> attributes) {
		super("httpie-request", attributes);
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		Map<String, Object> model = new HashMap<>();
		CliOperationRequest request = new CliOperationRequest(operation.getRequest());
		model.put("echoContent", getContentStandardIn(request));
		model.put("options", getOptions(request));
		model.put("url", getUrl(request));
		model.put("requestItems", getRequestItems(request));
		return model;
	}

	private Object getContentStandardIn(CliOperationRequest request) {
		String content = request.getContentAsString();
		if (StringUtils.hasText(content)) {
			return String.format("echo '%s' | ", content);
		}
		return "";
	}

	private String getOptions(CliOperationRequest request) {
		StringWriter options = new StringWriter();
		PrintWriter printer = new PrintWriter(options);
		writeOptions(request, printer);
		writeUserOptionIfNecessary(request, printer);
		writeMethodIfNecessary(request, printer);
		return options.toString();
	}

	private String getUrl(OperationRequest request) {
		return String.format("'%s'", request.getUri());
	}

	private String getRequestItems(CliOperationRequest request) {
		StringWriter requestItems = new StringWriter();
		PrintWriter printer = new PrintWriter(requestItems);
		writeFormDataIfNecessary(request, printer);
		writeHeaders(request, printer);
		writeParametersIfNecessary(request, printer);
		return requestItems.toString();
	}

	private void writeOptions(CliOperationRequest request, PrintWriter writer) {
		if (!request.getParts().isEmpty() || !request.getUniqueParameters().isEmpty()) {
			writer.print("--form ");
		}
	}

	private void writeUserOptionIfNecessary(CliOperationRequest request,
			PrintWriter writer) {
		String credentials = request.getBasicAuthCredentials();
		if (credentials != null) {
			writer.print(String.format("--auth '%s' ", credentials));
		}
	}

	private void writeMethodIfNecessary(OperationRequest request, PrintWriter writer) {
		writer.print(String.format("%s", request.getMethod().name()));
	}

	private void writeFormDataIfNecessary(OperationRequest request, PrintWriter writer) {
		for (OperationRequestPart part : request.getParts()) {
			writer.printf(" \\%n  '%s'", part.getName());
			if (!StringUtils.hasText(part.getSubmittedFileName())) {
				// https://github.com/jkbrzt/httpie/issues/342
				writer.printf("@<(echo '%s')", part.getContentAsString());
			}
			else {
				writer.printf("@'%s'", part.getSubmittedFileName());
			}
		}
	}

	private void writeHeaders(OperationRequest request, PrintWriter writer) {
		HttpHeaders headers = request.getHeaders();
		for (Entry<String, List<String>> entry : headers.entrySet()) {
			for (String header : entry.getValue()) {
				// HTTPie adds Content-Type automatically with --form
				if (!request.getParts().isEmpty()
						&& entry.getKey().equals(HttpHeaders.CONTENT_TYPE)
						&& header.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
					continue;
				}
				writer.print(String.format(" '%s:%s'", entry.getKey(), header));
			}
		}
	}

	private void writeParametersIfNecessary(CliOperationRequest request,
			PrintWriter writer) {
		if (StringUtils.hasText(request.getContentAsString())) {
			return;
		}
		if (!request.getParts().isEmpty()) {
			writeContentUsingParameters(request.getParameters(), writer);
		}
		else if (request.isPutOrPost()) {
			writeContentUsingParameters(request.getUniqueParameters(), writer);
		}
	}

	private void writeContentUsingParameters(Parameters parameters, PrintWriter writer) {
		for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
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
