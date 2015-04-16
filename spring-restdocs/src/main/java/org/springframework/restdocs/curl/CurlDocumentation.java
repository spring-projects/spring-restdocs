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

import static org.springframework.restdocs.util.IterableEnumeration.iterable;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.snippet.DocumentationWriter;
import org.springframework.restdocs.snippet.DocumentationWriter.DocumentationAction;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Static factory methods for documenting a RESTful API as if it were being driven using
 * the cURL command-line utility.
 * 
 * @author Andy Wilkinson
 * @author Yann Le Guern
 */
public abstract class CurlDocumentation {

	private CurlDocumentation() {

	}

	/**
	 * Produces a documentation snippet containing the request formatted as a cURL command
	 * 
	 * @param outputDir The directory to which snippet should be written
	 * @return the handler that will produce the snippet
	 */
	public static CurlSnippetResultHandler documentCurlRequest(String outputDir) {
		return new CurlSnippetResultHandler(outputDir, "request") {

			@Override
			public void handle(MvcResult result, DocumentationWriter writer)
					throws IOException {
				writer.shellCommand(new CurlRequestDocumentationAction(writer, result,
						getCurlConfiguration()));
			}
		};
	}

	/**
	 * Produces a documentation snippet containing the response formatted as the response
	 * to a cURL command
	 * 
	 * @param outputDir The directory to which snippet should be written
	 * @return the handler that will produce the snippet
	 */
	public static CurlSnippetResultHandler documentCurlResponse(String outputDir) {
		return new CurlSnippetResultHandler(outputDir, "response") {

			@Override
			public void handle(MvcResult result, DocumentationWriter writer)
					throws IOException {
				writer.codeBlock("http", new CurlResponseDocumentationAction(writer,
						result, getCurlConfiguration()));
			}
		};
	}

	/**
	 * Produces a documentation snippet containing both the request formatted as a cURL
	 * command and the response formatted formatted s the response to a cURL command.
	 * 
	 * @param outputDir The directory to which the snippet should be written
	 * @return the handler that will produce the snippet
	 */
	public static CurlSnippetResultHandler documentCurlRequestAndResponse(String outputDir) {
		return new CurlSnippetResultHandler(outputDir, "request-response") {

			@Override
			public void handle(MvcResult result, DocumentationWriter writer)
					throws IOException {
				writer.shellCommand(new CurlRequestDocumentationAction(writer, result,
						getCurlConfiguration()));
				writer.codeBlock("http", new CurlResponseDocumentationAction(writer,
						result, getCurlConfiguration()));
			}
		};
	}

	private static final class CurlRequestDocumentationAction implements
			DocumentationAction {

		private static final String SCHEME_HTTP = "http";

		private static final String SCHEME_HTTPS = "https";

		private static final int STANDARD_PORT_HTTP = 80;

		private static final int STANDARD_PORT_HTTPS = 443;

		private final DocumentationWriter writer;

		private final MvcResult result;

		private final CurlConfiguration curlConfiguration;

		CurlRequestDocumentationAction(DocumentationWriter writer, MvcResult result,
				CurlConfiguration curlConfiguration) {
			this.writer = writer;
			this.result = result;
			this.curlConfiguration = curlConfiguration;
		}

		@Override
		public void perform() throws IOException {
			MockHttpServletRequest request = this.result.getRequest();
			this.writer.print(String.format("curl %s://%s", request.getScheme(),
					request.getServerName()));

			if (isNonStandardPort(request)) {
				this.writer.print(String.format(":%d", request.getServerPort()));
			}

			if (StringUtils.hasText(request.getContextPath())) {
				this.writer.print(String.format("/%s", request.getContextPath()));
			}

			this.writer.print(getRequestUriWithQueryString(request));

			if (this.curlConfiguration.isIncludeResponseHeaders()) {
				this.writer.print(" -i");
			}

			if (!isGetRequest(request)) {
				this.writer.print(String.format(" -X %s", request.getMethod()));
			}

			for (String headerName : iterable(request.getHeaderNames())) {
				for (String header : iterable(request.getHeaders(headerName))) {
					this.writer
							.print(String.format(" -H \"%s: %s\"", headerName, header));
				}
			}

			if (request.getContentLengthLong() > 0) {
				this.writer.print(String.format(" -d '%s'", getContent(request)));
			}
			else if (isPostRequest(request)) {
				Map<String, String[]> parameters = request.getParameterMap();
				if (!parameters.isEmpty()) {
					this.writer.print(String
							.format(" -d '%s'", toQueryString(parameters)));
				}
			}

			this.writer.println();
		}

		private boolean isGetRequest(HttpServletRequest request) {
			return RequestMethod.GET == RequestMethod.valueOf(request.getMethod());
		}

		private boolean isPostRequest(HttpServletRequest request) {
			return RequestMethod.POST == RequestMethod.valueOf(request.getMethod());
		}

		private boolean isNonStandardPort(HttpServletRequest request) {
			return (SCHEME_HTTP.equals(request.getScheme()) && request.getServerPort() != STANDARD_PORT_HTTP)
					|| (SCHEME_HTTPS.equals(request.getScheme()) && request
							.getServerPort() != STANDARD_PORT_HTTPS);
		}

		private String getRequestUriWithQueryString(HttpServletRequest request) {
			StringBuilder sb = new StringBuilder();
			sb.append(request.getRequestURI());
			String queryString = getQueryString(request);
			if (StringUtils.hasText(queryString)) {
				sb.append('?').append(queryString);
			}
			return sb.toString();
		}

		private String getQueryString(HttpServletRequest request) {
			if (request.getQueryString() != null) {
				return request.getQueryString().replace("&", "\\&");
			}
			if (isGetRequest(request)) {
				return toQueryString(request.getParameterMap());
			}
			return null;
		}

		private static String toQueryString(Map<String, String[]> map) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String[]> entry : map.entrySet()) {
				for (String value : entry.getValue()) {
					if (sb.length() > 0) {
						sb.append("\\&");
					}
					sb.append(urlEncodeUTF8(entry.getKey())).append('=')
							.append(urlEncodeUTF8(value));
				}
			}
			return sb.toString();
		}

		private static String urlEncodeUTF8(String s) {
			try {
				return URLEncoder.encode(s, "UTF-8");
			}
			catch (UnsupportedEncodingException ex) {
				throw new IllegalStateException("Unable to URL encode " + s
						+ " using UTF-8", ex);
			}
		}

		private String getContent(MockHttpServletRequest request) throws IOException {
			StringWriter bodyWriter = new StringWriter();
			FileCopyUtils.copy(request.getReader(), bodyWriter);
			return bodyWriter.toString();
		}
	}

	private static final class CurlResponseDocumentationAction implements
			DocumentationAction {

		private final DocumentationWriter writer;

		private final MvcResult result;

		private final CurlConfiguration curlConfiguration;

		CurlResponseDocumentationAction(DocumentationWriter writer, MvcResult result,
				CurlConfiguration curlConfiguration) {
			this.writer = writer;
			this.result = result;
			this.curlConfiguration = curlConfiguration;
		}

		@Override
		public void perform() throws IOException {
			if (this.curlConfiguration.isIncludeResponseHeaders()) {
				HttpStatus status = HttpStatus.valueOf(this.result.getResponse()
						.getStatus());
				this.writer.println(String.format("HTTP/1.1 %d %s", status.value(),
						status.getReasonPhrase()));
				for (String headerName : this.result.getResponse().getHeaderNames()) {
					for (String header : this.result.getResponse().getHeaders(headerName)) {
						this.writer.println(String.format("%s: %s", headerName, header));
					}
				}
				this.writer.println();
			}
			this.writer.println(this.result.getResponse().getContentAsString());
		}
	}

}
