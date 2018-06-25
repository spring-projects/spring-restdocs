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

package org.springframework.restdocs.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.Parameters;
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
	 *
	 * @param commandFormatter The formatter
	 */
	protected HttpieRequestSnippet(CommandFormatter commandFormatter) {
		this(null, commandFormatter);
	}

	/**
	 * Creates a new {@code HttpieRequestSnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 * The given {@code commandFormaatter} will be used to format the HTTPie command.
	 *
	 * @param attributes The additional attributes
	 * @param commandFormatter The formatter for generating the snippet
	 */
	protected HttpieRequestSnippet(Map<String, Object> attributes,
			CommandFormatter commandFormatter) {
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
		Parameters uniqueParameters = request.getParameters()
				.getUniqueParameters(request.getUri());
		if (!uniqueParameters.isEmpty() && includeParametersInUri(request)) {
			return String.format("'%s%s%s'", request.getUri(),
					StringUtils.hasText(request.getUri().getRawQuery()) ? "&" : "?",
					uniqueParameters.toQueryString());
		}
		return String.format("'%s'", request.getUri());
	}

	private String getRequestItems(CliOperationRequest request) {
		List<String> lines = new ArrayList<>();

		writeFormDataIfNecessary(request, lines);
		writeHeaders(request, lines);
		writeCookies(request, lines);
		writeParametersIfNecessary(request, lines);

		return this.commandFormatter.format(lines);
	}

	private void writeOptions(OperationRequest request, PrintWriter writer) {
		if (!request.getParts().isEmpty()
				|| (!request.getParameters().getUniqueParameters(request.getUri())
						.isEmpty() && !includeParametersInUri(request)
						&& includeParametersAsFormOptions(request))) {
			writer.print("--form ");
		}
	}

	private boolean includeParametersInUri(OperationRequest request) {
		return request.getMethod() == HttpMethod.GET || (request.getContent().length > 0
				&& !MediaType.APPLICATION_FORM_URLENCODED
						.isCompatibleWith(request.getHeaders().getContentType()));
	}

	private boolean includeParametersAsFormOptions(OperationRequest request) {
		return request.getMethod() != HttpMethod.GET && (request.getContent().length == 0
				|| !MediaType.APPLICATION_FORM_URLENCODED
						.isCompatibleWith(request.getHeaders().getContentType()));
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

	private void writeFormDataIfNecessary(OperationRequest request, List<String> lines) {
		for (OperationRequestPart part : request.getParts()) {
			StringBuilder oneLine = new StringBuilder();
			oneLine.append(String.format("'%s'", part.getName()));
			if (!StringUtils.hasText(part.getSubmittedFileName())) {
				// https://github.com/jkbrzt/httpie/issues/342
				oneLine.append(String.format("@<(echo '%s')", part.getContentAsString()));
			}
			else {
				oneLine.append(String.format("@'%s'", part.getSubmittedFileName()));
			}

			lines.add(oneLine.toString());
		}
	}

	private void writeHeaders(OperationRequest request, List<String> lines) {
		HttpHeaders headers = request.getHeaders();
		for (Entry<String, List<String>> entry : headers.entrySet()) {
			for (String header : entry.getValue()) {
				// HTTPie adds Content-Type automatically with --form
				if (!request.getParts().isEmpty()
						&& entry.getKey().equals(HttpHeaders.CONTENT_TYPE)
						&& header.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
					continue;
				}
				lines.add(String.format("'%s:%s'", entry.getKey(), header));
			}
		}
	}

	private void writeCookies(OperationRequest request, List<String> lines) {
		for (RequestCookie cookie : request.getCookies()) {
			lines.add(
					String.format("'Cookie:%s=%s'", cookie.getName(), cookie.getValue()));
		}
	}

	private void writeParametersIfNecessary(CliOperationRequest request,
			List<String> lines) {
		if (StringUtils.hasText(request.getContentAsString())) {
			return;
		}
		if (!request.getParts().isEmpty()) {
			writeContentUsingParameters(request.getParameters(), lines);
		}
		else if (request.isPutOrPost()) {
			writeContentUsingParameters(
					request.getParameters().getUniqueParameters(request.getUri()), lines);
		}
	}

	private void writeContentUsingParameters(Parameters parameters, List<String> lines) {
		for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
			if (entry.getValue().isEmpty()) {
				lines.add(String.format("'%s='", entry.getKey()));
			}
			else {
				for (String value : entry.getValue()) {
					lines.add(String.format("'%s=%s'", entry.getKey(), value));
				}
			}
		}
	}

}
