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

package org.springframework.restdocs.curl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpMethod;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.StringUtils;

/**
 * A {@link Snippet} that documents the curl command for a request.
 *
 * @author Andy Wilkinson
 */
class CurlRequestSnippet extends TemplatedSnippet {

	CurlRequestSnippet() {
		this(null);
	}

	CurlRequestSnippet(Map<String, Object> attributes) {
		super("curl-request", attributes);
	}

	@Override
	public Map<String, Object> createModel(Operation operation) throws IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("arguments", getCurlCommandArguments(operation));
		return model;
	}

	private String getCurlCommandArguments(Operation operation) throws IOException {
		StringWriter command = new StringWriter();
		PrintWriter printer = new PrintWriter(command);
		printer.print("'");
		printer.print(operation.getRequest().getUri());
		printer.print("'");

		writeOptionToIncludeHeadersInOutput(printer);
		writeHttpMethodIfNecessary(operation.getRequest(), printer);
		writeHeaders(operation.getRequest(), printer);
		writePartsIfNecessary(operation.getRequest(), printer);

		writeContent(operation.getRequest(), printer);

		return command.toString();
	}

	private void writeOptionToIncludeHeadersInOutput(PrintWriter writer) {
		writer.print(" -i");
	}

	private void writeHttpMethodIfNecessary(OperationRequest request, PrintWriter writer) {
		if (!HttpMethod.GET.equals(request.getMethod())) {
			writer.print(String.format(" -X %s", request.getMethod()));
		}
	}

	private void writeHeaders(OperationRequest request, PrintWriter writer) {
		for (Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
			for (String header : entry.getValue()) {
				writer.print(String.format(" -H '%s: %s'", entry.getKey(), header));
			}
		}
	}

	private void writePartsIfNecessary(OperationRequest request, PrintWriter writer)
			throws IOException {
		for (OperationRequestPart part : request.getParts()) {
			writer.printf(" -F '%s=", part.getName());
			if (!StringUtils.hasText(part.getSubmittedFileName())) {
				writer.append(new String(part.getContent()));
			}
			else {
				writer.printf("@%s", part.getSubmittedFileName());
			}
			if (part.getHeaders().getContentType() != null) {
				writer.append(";type=").append(
						part.getHeaders().getContentType().toString());
			}

			writer.append("'");
		}
	}

	private void writeContent(OperationRequest request, PrintWriter writer)
			throws IOException {
		if (request.getContent().length > 0) {
			writer.print(String.format(" -d '%s'", new String(request.getContent())));
		}
		else if (!request.getParts().isEmpty()) {
			for (Entry<String, List<String>> entry : request.getParameters().entrySet()) {
				for (String value : entry.getValue()) {
					writer.print(String.format(" -F '%s=%s'", entry.getKey(), value));
				}
			}
		}
		else if (isPutOrPost(request)) {
			String queryString = request.getParameters().toQueryString();
			if (StringUtils.hasText(queryString)) {
				writer.print(String.format(" -d '%s'", queryString));
			}
		}
	}

	private boolean isPutOrPost(OperationRequest request) {
		return HttpMethod.PUT.equals(request.getMethod())
				|| HttpMethod.POST.equals(request.getMethod());
	}

}