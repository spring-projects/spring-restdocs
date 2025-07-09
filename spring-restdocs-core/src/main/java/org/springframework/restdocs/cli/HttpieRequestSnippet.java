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

package org.springframework.restdocs.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.FormParameters;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.RequestCookie;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A {@link Snippet} that documents the HTTPie command for a request.
 *
 * @author Raman Gupta
 * @author Andy Wilkinson
 * @author Tomasz Kopczynski
 * @since 1.1.0
 * @see CliDocumentation#httpieRequest()
 * @see CliDocumentation#httpieRequest(Map)
 */
public class HttpieRequestSnippet extends TemplatedSnippet {

	private final CommandFormatter commandFormatter;

	/**
	 * Creates a new {@code HttpieRequestSnippet} that will use the given
	 * {@code commandFormatter} to format the HTTPie command.
	 * @param commandFormatter the formatter
	 */
	protected HttpieRequestSnippet(CommandFormatter commandFormatter) {
		this(null, commandFormatter);
	}

	/**
	 * Creates a new {@code HttpieRequestSnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 * The given {@code commandFormaatter} will be used to format the HTTPie command.
	 * @param attributes the additional attributes
	 * @param commandFormatter the formatter for generating the snippet
	 */
	protected HttpieRequestSnippet(@Nullable Map<String, Object> attributes, CommandFormatter commandFormatter) {
		super("httpie-request", attributes);
		Assert.notNull(commandFormatter, "Command formatter must not be null");
		this.commandFormatter = commandFormatter;
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
		if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(request.getHeaders().getContentType())) {
			return "";
		}
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
		List<String> lines = new ArrayList<>();

		writeHeaders(request, lines);
		writeCookies(request, lines);
		writeFormDataIfNecessary(request, lines);

		return this.commandFormatter.format(lines);
	}

	private void writeOptions(OperationRequest request, PrintWriter writer) {
		if (!request.getParts().isEmpty()) {
			writer.print("--multipart ");
		}
		else if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(request.getHeaders().getContentType())) {
			writer.print("--form ");
		}
	}

	private void writeUserOptionIfNecessary(CliOperationRequest request, PrintWriter writer) {
		String credentials = request.getBasicAuthCredentials();
		if (credentials != null) {
			writer.print(String.format("--auth '%s' ", credentials));
		}
	}

	private void writeMethodIfNecessary(OperationRequest request, PrintWriter writer) {
		writer.print(String.format("%s", request.getMethod().name()));
	}

	private void writeFormDataIfNecessary(OperationRequest request, List<String> lines) {
		if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(request.getHeaders().getContentType())) {
			FormParameters.from(request)
				.forEach((key, values) -> values.forEach((value) -> lines.add(String.format("'%s=%s'", key, value))));
		}
		else {
			for (OperationRequestPart part : request.getParts()) {
				StringBuilder oneLine = new StringBuilder();
				oneLine.append(String.format("'%s'", part.getName()));
				if (!StringUtils.hasText(part.getSubmittedFileName())) {
					oneLine.append(String.format("='%s'", part.getContentAsString()));
				}
				else {
					oneLine.append(String.format("@'%s'", part.getSubmittedFileName()));
				}

				lines.add(oneLine.toString());
			}
		}
	}

	private void writeHeaders(OperationRequest request, List<String> lines) {
		HttpHeaders headers = request.getHeaders();
		for (Entry<String, List<String>> entry : headers.headerSet()) {
			if (entry.getKey().equals(HttpHeaders.CONTENT_TYPE)) {
				MediaType contentType = headers.getContentType();
				if (contentType != null && contentType.isCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED)) {
					continue;
				}
			}
			for (String header : entry.getValue()) {
				// HTTPie adds Content-Type automatically with --form
				if (!request.getParts().isEmpty() && entry.getKey().equals(HttpHeaders.CONTENT_TYPE)
						&& header.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
					continue;
				}
				lines.add(String.format("'%s:%s'", entry.getKey(), header));
			}
		}
	}

	private void writeCookies(OperationRequest request, List<String> lines) {
		for (RequestCookie cookie : request.getCookies()) {
			lines.add(String.format("'Cookie:%s=%s'", cookie.getName(), cookie.getValue()));
		}
	}

}
