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

package org.springframework.restdocs.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.SnippetWritingResultHandler;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.util.DocumentableHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Static factory methods for documenting a RESTful API's HTTP requests.
 * 
 * @author Andy Wilkinson
 * @author Jonathan Pearlin
 */
public abstract class HttpDocumentation {

	private static final String MULTIPART_BOUNDARY = "6o2knFse3p53ty9dmcQvWAIx1zInP11uCfbm";

	private HttpDocumentation() {

	}

	/**
	 * Produces a documentation snippet containing the request formatted as an HTTP
	 * request
	 * 
	 * @param outputDir The directory to which snippet should be written
	 * @param attributes Attributes made available during rendering of the HTTP requst
	 * snippet
	 * @return the handler that will produce the snippet
	 */
	public static SnippetWritingResultHandler documentHttpRequest(String outputDir,
			Map<String, Object> attributes) {
		return new HttpRequestWritingResultHandler(outputDir, attributes);
	}

	/**
	 * Produces a documentation snippet containing the response formatted as the HTTP
	 * response sent by the server
	 * 
	 * @param outputDir The directory to which snippet should be written
	 * @param attributes Attributes made available during rendering of the HTTP response
	 * snippet
	 * @return the handler that will produce the snippet
	 */
	public static SnippetWritingResultHandler documentHttpResponse(String outputDir,
			Map<String, Object> attributes) {
		return new HttpResponseWritingResultHandler(outputDir, attributes);

	}

	private static final class HttpRequestWritingResultHandler extends
			SnippetWritingResultHandler {

		private HttpRequestWritingResultHandler(String outputDir,
				Map<String, Object> attributes) {
			super(outputDir, "http-request", attributes);
		}

		@Override
		public void handle(MvcResult result, PrintWriter writer) throws IOException {
			DocumentableHttpServletRequest request = new DocumentableHttpServletRequest(
					result.getRequest());
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("method", result.getRequest().getMethod());
			context.put("path", request.getRequestUriWithQueryString());
			context.put("headers", getHeaders(request));
			context.put("requestBody", getRequestBody(request));
			context.putAll(getAttributes());

			TemplateEngine templateEngine = (TemplateEngine) result.getRequest()
					.getAttribute(TemplateEngine.class.getName());

			writer.print(templateEngine.compileTemplate("http-request").render(context));
		}

		private List<Map<String, String>> getHeaders(
				DocumentableHttpServletRequest request) {
			List<Map<String, String>> headers = new ArrayList<>();
			if (requiresHostHeader(request)) {
				headers.add(header(HttpHeaders.HOST, request.getHost()));
			}

			for (Entry<String, List<String>> header : request.getHeaders().entrySet()) {
				for (String value : header.getValue()) {
					if (header.getKey() == HttpHeaders.CONTENT_TYPE
							&& request.isMultipartRequest()) {
						headers.add(header(header.getKey(), String.format(
								"%s; boundary=%s", value, MULTIPART_BOUNDARY)));
					}
					else {
						headers.add(header(header.getKey(), value));
					}

				}
			}
			if (requiresFormEncodingContentType(request)) {
				headers.add(header(HttpHeaders.CONTENT_TYPE,
						MediaType.APPLICATION_FORM_URLENCODED_VALUE));
			}
			return headers;
		}

		private String getRequestBody(DocumentableHttpServletRequest request)
				throws IOException {
			StringWriter httpRequest = new StringWriter();
			PrintWriter writer = new PrintWriter(httpRequest);
			if (request.getContentLength() > 0) {
				writer.println();
				writer.print(request.getContentAsString());
			}
			else if (request.isPostRequest() || request.isPutRequest()) {
				if (request.isMultipartRequest()) {
					writeParts(request, writer);
				}
				else {
					String queryString = request.getParameterMapAsQueryString();
					if (StringUtils.hasText(queryString)) {
						writer.println();
						writer.print(queryString);
					}
				}
			}
			return httpRequest.toString();
		}

		private void writeParts(DocumentableHttpServletRequest request, PrintWriter writer)
				throws IOException {
			writer.println();
			for (Entry<String, String[]> parameter : request.getParameterMap().entrySet()) {
				for (String value : parameter.getValue()) {
					writePartBoundary(writer);
					writePart(parameter.getKey(), value, null, writer);
					writer.println();
				}
			}
			for (Entry<String, List<MultipartFile>> entry : request.getMultipartFiles()
					.entrySet()) {
				for (MultipartFile file : entry.getValue()) {
					writePartBoundary(writer);
					writePart(file, writer);
					writer.println();
				}
			}
			writeMultipartEnd(writer);
		}

		private void writePartBoundary(PrintWriter writer) {
			writer.printf("--%s%n", MULTIPART_BOUNDARY);
		}

		private void writePart(String name, String value, String contentType,
				PrintWriter writer) {
			writer.printf("Content-Disposition: form-data; name=%s%n", name);
			if (StringUtils.hasText(contentType)) {
				writer.printf("Content-Type: %s%n", contentType);
			}
			writer.println();
			writer.print(value);
		}

		private void writePart(MultipartFile part, PrintWriter writer) throws IOException {
			writePart(part.getName(), new String(part.getBytes()), part.getContentType(),
					writer);
		}

		private void writeMultipartEnd(PrintWriter writer) {
			writer.printf("--%s--", MULTIPART_BOUNDARY);
		}

		private boolean requiresHostHeader(DocumentableHttpServletRequest request) {
			return request.getHeaders().get(HttpHeaders.HOST) == null;
		}

		private boolean requiresFormEncodingContentType(
				DocumentableHttpServletRequest request) {
			return request.getHeaders().getContentType() == null
					&& (request.isPostRequest() || request.isPutRequest())
					&& StringUtils.hasText(request.getParameterMapAsQueryString());
		}

		private Map<String, String> header(String name, String value) {
			Map<String, String> header = new HashMap<>();
			header.put("name", name);
			header.put("value", value);
			return header;
		}
	}

	private static final class HttpResponseWritingResultHandler extends
			SnippetWritingResultHandler {

		private HttpResponseWritingResultHandler(String outputDir,
				Map<String, Object> attributes) {
			super(outputDir, "http-response", attributes);
		}

		@Override
		public void handle(MvcResult result, PrintWriter writer) throws IOException {
			HttpStatus status = HttpStatus.valueOf(result.getResponse().getStatus());
			Map<String, Object> context = new HashMap<String, Object>();
			context.put(
					"responseBody",
					StringUtils.hasLength(result.getResponse().getContentAsString()) ? String
							.format("%n%s", result.getResponse().getContentAsString())
							: "");
			context.put("statusCode", status.value());
			context.put("statusReason", status.getReasonPhrase());

			List<Map<String, String>> headers = new ArrayList<>();
			context.put("headers", headers);

			for (String headerName : result.getResponse().getHeaderNames()) {
				for (String header : result.getResponse().getHeaders(headerName)) {
					headers.add(header(headerName, header));
				}
			}

			context.putAll(getAttributes());

			TemplateEngine templateEngine = (TemplateEngine) result.getRequest()
					.getAttribute(TemplateEngine.class.getName());

			writer.print(templateEngine.compileTemplate("http-response").render(context));

		}

		private Map<String, String> header(String name, String value) {
			Map<String, String> header = new HashMap<>();
			header.put("name", name);
			header.put("value", value);
			return header;
		}
	}

}
