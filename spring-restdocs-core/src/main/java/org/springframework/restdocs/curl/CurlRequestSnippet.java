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
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

/**
 * A {@link Snippet} that documents the curl command for a request.
 *
 * @author Andy Wilkinson
 * @author Paul-Christian Volkmer
 * @see CurlDocumentation#curlRequest()
 * @see CurlDocumentation#curlRequest(Map)
 */
public class CurlRequestSnippet extends TemplatedSnippet {

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
		Map<String, Object> model = new HashMap<String, Object>();
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
		writeOptionToIncludeHeadersInOutput(printer);
		writeHttpBasicAuthorization(operation.getRequest(), printer);
		writeHttpMethodIfNecessary(operation.getRequest(), printer);
		writeHeaders(operation.getRequest(), printer);
		writePartsIfNecessary(operation.getRequest(), printer);

		writeContent(operation.getRequest(), printer);

		return command.toString();
	}

	private void writeOptionToIncludeHeadersInOutput(PrintWriter writer) {
		writer.print("-i");
	}

	private void writeHttpMethodIfNecessary(OperationRequest request, PrintWriter writer) {
		if (!HttpMethod.GET.equals(request.getMethod())) {
			writer.print(String.format(" -X %s", request.getMethod()));
		}
	}

	private void writeHttpBasicAuthorization(OperationRequest request, PrintWriter writer) {
		for (Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
			for (String header : entry.getValue()) {
				if (isAuthBasicHeader(entry.getKey(), header)) {
					String auth = new String(Base64Utils.decodeFromString(header.replace("Basic", "").trim()));
					writer.print(String.format(" -u '%s'", auth));
					break;
				}
			}
		}
	}

	private void writeHeaders(OperationRequest request, PrintWriter writer) {
		for (Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
			for (String header : entry.getValue()) {
				if (isAuthBasicHeader(entry.getKey(), header)) {
					continue;
				}
				writer.print(String.format(" -H '%s: %s'", entry.getKey(), header));
			}
		}
	}

	private void writePartsIfNecessary(OperationRequest request, PrintWriter writer) {
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

	private void writeContent(OperationRequest request, PrintWriter writer) {
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

	private boolean isAuthBasicHeader(String key, String value) {
		return ("Authorization".equals(key) && value.startsWith("Basic"));
	}

}
