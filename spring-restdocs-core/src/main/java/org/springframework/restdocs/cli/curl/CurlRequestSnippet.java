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

package org.springframework.restdocs.cli.curl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.restdocs.cli.AbstractCliSnippet;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.util.StringUtils;

/**
 * A {@link Snippet} that documents the curl command for a request.
 *
 * @author Andy Wilkinson
 * @author Paul-Christian Volkmer
 * @see CurlDocumentation#curlRequest()
 * @see CurlDocumentation#curlRequest(Map)
 */
public class CurlRequestSnippet extends AbstractCliSnippet {

	/**
	 * Creates a new {@code CurlRequestSnippet} with no additional attributes.
	 */
	protected CurlRequestSnippet() {
		this(null);
	}

	/**
	 * Creates a new {@code CurlRequestSnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 *
	 * @param attributes The additional attributes
	 */
	protected CurlRequestSnippet(Map<String, Object> attributes) {
		super("curl-request", attributes);
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		Map<String, Object> model = new HashMap<>();
		model.put("url", getUrl(operation));
		model.put("options", getOptions(operation));
		return model;
	}

	private String getUrl(Operation operation) {
		return String.format("'%s'", operation.getRequest().getUri());
	}

	private String getOptions(Operation operation) {
		StringWriter command = new StringWriter();
		PrintWriter printer = new PrintWriter(command);
		writeIncludeHeadersInOutputOption(printer);
		writeUserOptionIfNecessary(operation.getRequest(), printer);
		writeHttpMethodIfNecessary(operation.getRequest(), printer);
		writeHeaders(operation.getRequest().getHeaders(), printer);
		writePartsIfNecessary(operation.getRequest(), printer);
		writeContent(operation.getRequest(), printer);

		return command.toString();
	}

	private void writeIncludeHeadersInOutputOption(PrintWriter writer) {
		writer.print("-i");
	}

	private void writeUserOptionIfNecessary(OperationRequest request,
			PrintWriter writer) {
		List<String> headerValue = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
		if (isBasicAuthHeader(headerValue)) {
			String credentials = decodeBasicAuthHeader(headerValue);
			writer.print(String.format(" -u '%s'", credentials));
		}
	}

	private void writeHttpMethodIfNecessary(OperationRequest request,
			PrintWriter writer) {
		if (!HttpMethod.GET.equals(request.getMethod())) {
			writer.print(String.format(" -X %s", request.getMethod()));
		}
	}

	private void writeHeaders(HttpHeaders headers, PrintWriter writer) {
		for (Entry<String, List<String>> entry : headers.entrySet()) {
			if (allowedHeader(entry)) {
				for (String header : entry.getValue()) {
					writer.print(String.format(" -H '%s: %s'", entry.getKey(), header));
				}
			}
		}
	}

	private void writePartsIfNecessary(OperationRequest request, PrintWriter writer) {
		for (OperationRequestPart part : request.getParts()) {
			writer.printf(" -F '%s=", part.getName());
			if (!StringUtils.hasText(part.getSubmittedFileName())) {
				writer.append(part.getContentAsString());
			}
			else {
				writer.printf("@%s", part.getSubmittedFileName());
			}
			if (part.getHeaders().getContentType() != null) {
				writer.append(";type=")
						.append(part.getHeaders().getContentType().toString());
			}

			writer.append("'");
		}
	}

	private void writeContent(OperationRequest request, PrintWriter writer) {
		String content = request.getContentAsString();
		if (StringUtils.hasText(content)) {
			writer.print(String.format(" -d '%s'", content));
		}
		else if (!request.getParts().isEmpty()) {
			for (Entry<String, List<String>> entry : request.getParameters().entrySet()) {
				for (String value : entry.getValue()) {
					writer.print(String.format(" -F '%s=%s'", entry.getKey(), value));
				}
			}
		}
		else if (isPutOrPost(request)) {
			writeContentUsingParameters(request, writer);
		}
	}

	private void writeContentUsingParameters(OperationRequest request,
			PrintWriter writer) {
		Parameters uniqueParameters = getUniqueParameters(request);
		String queryString = uniqueParameters.toQueryString();
		if (StringUtils.hasText(queryString)) {
			writer.print(String.format(" -d '%s'", queryString));
		}
	}
}
