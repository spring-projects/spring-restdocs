/*
 * Copyright 2014-present the original author or authors.
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

package org.springframework.restdocs.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.RequestCookie;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.StringUtils;

/**
 * A {@link Snippet} that documents an HTTP request.
 *
 * @author Andy Wilkinson
 * @see HttpDocumentation#httpRequest()
 * @see HttpDocumentation#httpRequest(Map)
 */
public class HttpRequestSnippet extends TemplatedSnippet {

	private static final String MULTIPART_BOUNDARY = "6o2knFse3p53ty9dmcQvWAIx1zInP11uCfbm";

	/**
	 * Creates a new {@code HttpRequestSnippet} with no additional attributes.
	 */
	protected HttpRequestSnippet() {
		this(null);
	}

	/**
	 * Creates a new {@code HttpRequestSnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 * @param attributes the additional attributes
	 */
	protected HttpRequestSnippet(@Nullable Map<String, Object> attributes) {
		super("http-request", attributes);
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		Map<String, Object> model = new HashMap<>();
		model.put("method", operation.getRequest().getMethod());
		model.put("path", getPath(operation.getRequest()));
		model.put("headers", getHeaders(operation.getRequest()));
		model.put("requestBody", getRequestBody(operation.getRequest()));
		return model;
	}

	private String getPath(OperationRequest request) {
		String path = request.getUri().getRawPath();
		String queryString = request.getUri().getRawQuery();
		if (StringUtils.hasText(queryString)) {
			path = path + "?" + queryString;
		}
		return path;
	}

	private boolean includeParametersInUri(OperationRequest request) {
		HttpMethod method = request.getMethod();
		return (method != HttpMethod.PUT && method != HttpMethod.POST && method != HttpMethod.PATCH)
				|| (request.getContent().length > 0 && !MediaType.APPLICATION_FORM_URLENCODED
					.isCompatibleWith(request.getHeaders().getContentType()));
	}

	private List<Map<String, String>> getHeaders(OperationRequest request) {
		List<Map<String, String>> headers = new ArrayList<>();

		for (Entry<String, List<String>> header : request.getHeaders().headerSet()) {
			for (String value : header.getValue()) {
				if (HttpHeaders.CONTENT_TYPE.equals(header.getKey()) && !request.getParts().isEmpty()) {
					headers.add(header(header.getKey(), String.format("%s; boundary=%s", value, MULTIPART_BOUNDARY)));
				}
				else {
					headers.add(header(header.getKey(), value));
				}

			}
		}

		List<String> cookies = new ArrayList<>();
		for (RequestCookie cookie : request.getCookies()) {
			cookies.add(String.format("%s=%s", cookie.getName(), cookie.getValue()));
		}
		if (!cookies.isEmpty()) {
			headers.add(header(HttpHeaders.COOKIE, String.join("; ", cookies)));
		}

		if (requiresFormEncodingContentTypeHeader(request)) {
			headers.add(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE));
		}
		return headers;
	}

	private String getRequestBody(OperationRequest request) {
		StringWriter httpRequest = new StringWriter();
		PrintWriter writer = new PrintWriter(httpRequest);
		String content = request.getContentAsString();
		if (StringUtils.hasText(content)) {
			writer.printf("%n%s", content);
		}
		else if (isPutPostOrPatch(request)) {
			if (!request.getParts().isEmpty()) {
				writeParts(request, writer);
			}
		}
		return httpRequest.toString();
	}

	private boolean isPutPostOrPatch(OperationRequest request) {
		return HttpMethod.PUT.equals(request.getMethod()) || HttpMethod.POST.equals(request.getMethod())
				|| HttpMethod.PATCH.equals(request.getMethod());
	}

	private void writeParts(OperationRequest request, PrintWriter writer) {
		writer.println();
		for (OperationRequestPart part : request.getParts()) {
			writePartBoundary(writer);
			writePart(part, writer);
			writer.println();
		}
		writeMultipartEnd(writer);
	}

	private void writePartBoundary(PrintWriter writer) {
		writer.printf("--%s%n", MULTIPART_BOUNDARY);
	}

	private void writePart(OperationRequestPart part, PrintWriter writer) {
		writePart(part.getName(), part.getContentAsString(), part.getSubmittedFileName(),
				part.getHeaders().getContentType(), writer);
	}

	private void writePart(String name, String value, @Nullable String filename, @Nullable MediaType contentType,
			PrintWriter writer) {
		writer.printf("Content-Disposition: form-data; name=%s", name);
		if (StringUtils.hasText(filename)) {
			writer.printf("; filename=%s", filename);
		}
		writer.printf("%n");
		if (contentType != null) {
			writer.printf("Content-Type: %s%n", contentType);
		}
		writer.println();
		writer.print(value);
	}

	private void writeMultipartEnd(PrintWriter writer) {
		writer.printf("--%s--", MULTIPART_BOUNDARY);
	}

	private boolean requiresFormEncodingContentTypeHeader(OperationRequest request) {
		return request.getHeaders().get(HttpHeaders.CONTENT_TYPE) == null && isPutPostOrPatch(request)
				&& request.getContent().length > 0 && !includeParametersInUri(request);
	}

	private Map<String, String> header(String name, String value) {
		Map<String, String> header = new HashMap<>();
		header.put("name", name);
		header.put("value", value);
		return header;
	}

}
