/*
 * Copyright 2014-2025 the original author or authors.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.RequestCookie;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * A {@link Snippet} that documents the curl command for a request.
 *
 * @author Andy Wilkinson
 * @author Paul-Christian Volkmer
 * @author Tomasz Kopczynski
 * @since 1.1.0
 * @see CliDocumentation#curlRequest()
 * @see CliDocumentation#curlRequest(CommandFormatter)
 * @see CliDocumentation#curlRequest(Map)
 */
public class CurlRequestSnippet extends TemplatedSnippet {

	private final CommandFormatter commandFormatter;

	/**
	 * Creates a new {@code CurlRequestSnippet} that will use the given
	 * {@code commandFormatter} to format the curl command.
	 * @param commandFormatter the formatter
	 */
	protected CurlRequestSnippet(CommandFormatter commandFormatter) {
		this(null, commandFormatter);
	}

	/**
	 * Creates a new {@code CurlRequestSnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 * The given {@code commandFormaatter} will be used to format the curl command.
	 * @param attributes the additional attributes
	 * @param commandFormatter the formatter for generating the snippet
	 */
	protected CurlRequestSnippet(Map<String, Object> attributes, CommandFormatter commandFormatter) {
		super("curl-request", attributes);
		Assert.notNull(commandFormatter, "Command formatter must not be null");
		this.commandFormatter = commandFormatter;
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		Map<String, Object> model = new HashMap<>();
		model.put("url", getUrl(operation));
		model.put("options", getOptions(operation));
		return model;
	}

	private String getUrl(Operation operation) {
		OperationRequest request = operation.getRequest();
		return String.format("'%s'", request.getUri());
	}

	private String getOptions(Operation operation) {
		StringBuilder builder = new StringBuilder();
		writeIncludeHeadersInOutputOption(builder);

		CliOperationRequest request = new CliOperationRequest(operation.getRequest());
		writeUserOptionIfNecessary(request, builder);
		writeHttpMethod(request, builder);

		List<String> additionalLines = new ArrayList<>();
		writeHeaders(request, additionalLines);
		writeCookies(request, additionalLines);
		writePartsIfNecessary(request, additionalLines);
		writeContent(request, additionalLines);

		builder.append(this.commandFormatter.format(additionalLines));

		return builder.toString();
	}

	private void writeCookies(CliOperationRequest request, List<String> lines) {
		if (!CollectionUtils.isEmpty(request.getCookies())) {
			StringBuilder cookiesBuilder = new StringBuilder();
			for (RequestCookie cookie : request.getCookies()) {
				if (cookiesBuilder.length() > 0) {
					cookiesBuilder.append(";");
				}
				cookiesBuilder.append(String.format("%s=%s", cookie.getName(), cookie.getValue()));
			}
			lines.add(String.format("--cookie '%s'", cookiesBuilder.toString()));
		}
	}

	private void writeIncludeHeadersInOutputOption(StringBuilder builder) {
		builder.append("-i");
	}

	private void writeUserOptionIfNecessary(CliOperationRequest request, StringBuilder builder) {
		String credentials = request.getBasicAuthCredentials();
		if (credentials != null) {
			builder.append(String.format(" -u '%s'", credentials));
		}
	}

	private void writeHttpMethod(OperationRequest request, StringBuilder builder) {
		builder.append(String.format(" -X %s", request.getMethod()));
	}

	private void writeHeaders(CliOperationRequest request, List<String> lines) {
		for (Entry<String, List<String>> entry : request.getHeaders().headerSet()) {
			for (String header : entry.getValue()) {
				if (StringUtils.hasText(request.getContentAsString()) && HttpHeaders.CONTENT_TYPE.equals(entry.getKey())
						&& MediaType.APPLICATION_FORM_URLENCODED.equals(request.getHeaders().getContentType())) {
					continue;
				}
				lines.add(String.format("-H '%s: %s'", entry.getKey(), header));
			}
		}
	}

	private void writePartsIfNecessary(OperationRequest request, List<String> lines) {
		for (OperationRequestPart part : request.getParts()) {
			StringBuilder oneLine = new StringBuilder();
			oneLine.append(String.format("-F '%s=", part.getName()));
			if (!StringUtils.hasText(part.getSubmittedFileName())) {
				oneLine.append(part.getContentAsString());
			}
			else {
				oneLine.append(String.format("@%s", part.getSubmittedFileName()));
			}
			if (part.getHeaders().getContentType() != null) {
				oneLine.append(";type=");
				oneLine.append(part.getHeaders().getContentType().toString());
			}
			oneLine.append("'");
			lines.add(oneLine.toString());
		}
	}

	private void writeContent(CliOperationRequest request, List<String> lines) {
		String content = request.getContentAsString();
		if (StringUtils.hasText(content)) {
			lines.add(String.format("-d '%s'", content));
		}
	}

}
