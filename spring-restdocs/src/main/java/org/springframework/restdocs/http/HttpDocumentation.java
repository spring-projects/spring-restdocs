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
import java.util.List;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.DocumentationWriter;
import org.springframework.restdocs.snippet.DocumentationWriter.DocumentationAction;
import org.springframework.restdocs.snippet.SnippetWritingResultHandler;
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

	private HttpDocumentation() {

	}

	/**
	 * Produces a documentation snippet containing the request formatted as an HTTP
	 * request
	 * 
	 * @param outputDir The directory to which snippet should be written
	 * @return the handler that will produce the snippet
	 */
	public static SnippetWritingResultHandler documentHttpRequest(String outputDir) {
		return new SnippetWritingResultHandler(outputDir, "http-request") {

			@Override
			public void handle(MvcResult result, DocumentationWriter writer)
					throws IOException {
				writer.codeBlock("http", new HttpRequestDocumentationAction(writer,
						result));
			}
		};
	}

	/**
	 * Produces a documentation snippet containing the response formatted as the HTTP
	 * response sent by the server
	 * 
	 * @param outputDir The directory to which snippet should be written
	 * @return the handler that will produce the snippet
	 */
	public static SnippetWritingResultHandler documentHttpResponse(String outputDir) {
		return new SnippetWritingResultHandler(outputDir, "http-response") {

			@Override
			public void handle(MvcResult result, DocumentationWriter writer)
					throws IOException {
				writer.codeBlock("http", new HttpResponseDocumentationAction(writer,
						result));
			}
		};
	}

	private static class HttpRequestDocumentationAction implements DocumentationAction {

		private static final String MULTIPART_BOUNDARY = "6o2knFse3p53ty9dmcQvWAIx1zInP11uCfbm";

		private final DocumentationWriter writer;

		private final MvcResult result;

		HttpRequestDocumentationAction(DocumentationWriter writer, MvcResult result) {
			this.writer = writer;
			this.result = result;
		}

		@Override
		public void perform() throws IOException {
			DocumentableHttpServletRequest request = new DocumentableHttpServletRequest(
					this.result.getRequest());
			this.writer.printf("%s %s HTTP/1.1%n", request.getMethod(),
					request.getRequestUriWithQueryString());
			if (requiresHostHeader(request)) {
				writeHeader(HttpHeaders.HOST, request.getHost());
			}
			for (Entry<String, List<String>> header : request.getHeaders().entrySet()) {
				for (String value : header.getValue()) {
					if (header.getKey() == HttpHeaders.CONTENT_TYPE
							&& request.isMultipartRequest()) {
						writeHeader(header.getKey(), String.format("%s; boundary=%s",
								value, MULTIPART_BOUNDARY));
					}
					else {
						this.writer.printf("%s: %s%n", header.getKey(), value);
					}

				}
			}
			if (requiresFormEncodingContentType(request)) {
				writeHeader(HttpHeaders.CONTENT_TYPE,
						MediaType.APPLICATION_FORM_URLENCODED_VALUE);
			}
			this.writer.println();
			if (request.getContentLength() > 0) {
				this.writer.println(request.getContentAsString());
			}
			else if (request.isPostRequest() || request.isPutRequest()) {
				String queryString = request.getParameterMapAsQueryString();
				if (StringUtils.hasText(queryString)) {
					this.writer.println(queryString);
				}
				if (request.isMultipartRequest()) {
					writeParts(request);
				}
			}
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

		private void writeHeader(String name, String value) {
			this.writer.printf("%s: %s%n", name, value);
		}

		private void writeParts(DocumentableHttpServletRequest request)
				throws IOException {
			for (Entry<String, List<MultipartFile>> entry : request.getMultipartFiles()
					.entrySet()) {
				for (MultipartFile file : entry.getValue()) {
					writePartBoundary();
					writePart(file);
					this.writer.println();
				}
			}
			writeMultipartEnd();
		}

		private void writePartBoundary() {
			this.writer.printf("--%s%n", MULTIPART_BOUNDARY);
		}

		private void writePart(MultipartFile part) throws IOException {
			this.writer.printf("Content-Disposition: form-data; name=%s%n",
					part.getName());
			if (StringUtils.hasText(part.getContentType())) {
				this.writer.printf("Content-Type: %s%n", part.getContentType());
			}
			this.writer.println();
			this.writer.print(new String(part.getBytes()));
		}

		private void writeMultipartEnd() {
			this.writer.printf("--%s--%n", MULTIPART_BOUNDARY);
		}
	}

	private static final class HttpResponseDocumentationAction implements
			DocumentationAction {

		private final DocumentationWriter writer;

		private final MvcResult result;

		HttpResponseDocumentationAction(DocumentationWriter writer, MvcResult result) {
			this.writer = writer;
			this.result = result;
		}

		@Override
		public void perform() throws IOException {
			HttpStatus status = HttpStatus.valueOf(this.result.getResponse().getStatus());
			this.writer.println(String.format("HTTP/1.1 %d %s", status.value(),
					status.getReasonPhrase()));
			for (String headerName : this.result.getResponse().getHeaderNames()) {
				for (String header : this.result.getResponse().getHeaders(headerName)) {
					this.writer.println(String.format("%s: %s", headerName, header));
				}
			}
			this.writer.println();
			String content = this.result.getResponse().getContentAsString();
			if (StringUtils.hasText(content)) {
				this.writer.println(content);
			}
		}
	}

}
