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
import java.util.List;
import java.util.Map.Entry;

import org.springframework.restdocs.snippet.DocumentationWriter;
import org.springframework.restdocs.snippet.DocumentationWriter.DocumentationAction;
import org.springframework.restdocs.snippet.SnippetWritingResultHandler;
import org.springframework.restdocs.util.DocumentableHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StringUtils;

/**
 * Static factory methods for documenting a RESTful API as if it were being driven using
 * the cURL command-line utility.
 *
 * @author Andy Wilkinson
 * @author Yann Le Guern
 * @author Dmitriy Mayboroda
 * @author Jonathan Pearlin
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
	public static SnippetWritingResultHandler documentCurlRequest(String outputDir) {
		return new SnippetWritingResultHandler(outputDir, "curl-request") {

			@Override
			public void handle(MvcResult result, DocumentationWriter writer)
					throws IOException {
				writer.shellCommand(new CurlRequestDocumentationAction(writer, result));
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

		CurlRequestDocumentationAction(DocumentationWriter writer, MvcResult result) {
			this.writer = writer;
			this.result = result;
		}

		@Override
		public void perform() throws IOException {
			DocumentableHttpServletRequest request = new DocumentableHttpServletRequest(
					this.result.getRequest());
			this.writer.print(String.format("curl '%s://%s", request.getScheme(),
					request.getHost()));

			if (isNonStandardPort(request)) {
				this.writer.print(String.format(":%d", request.getPort()));
			}

			if (StringUtils.hasText(request.getContextPath())) {
				this.writer.print(String.format(
						request.getContextPath().startsWith("/") ? "%s" : "/%s",
						request.getContextPath()));
			}

			this.writer.print(request.getRequestUriWithQueryString());

			this.writer.print("' -i");

			if (!request.isGetRequest()) {
				this.writer.print(String.format(" -X %s", request.getMethod()));
			}

			for (Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
				for (String header : entry.getValue()) {
					this.writer.print(String.format(" -H '%s: %s'", entry.getKey(),
							header));
				}
			}

			if (request.getContentLength() > 0) {
				this.writer
						.print(String.format(" -d '%s'", request.getContentAsString()));
			}
			else if (request.isPostRequest() || request.isPutRequest()) {
				String queryString = request.getParameterMapAsQueryString();
				if (StringUtils.hasText(queryString)) {
					this.writer.print(String.format(" -d '%s'", queryString));
				}
			}

			this.writer.println();
		}

		private boolean isNonStandardPort(DocumentableHttpServletRequest request) {
			return (SCHEME_HTTP.equals(request.getScheme()) && request.getPort() != STANDARD_PORT_HTTP)
					|| (SCHEME_HTTPS.equals(request.getScheme()) && request.getPort() != STANDARD_PORT_HTTPS);
		}
	}

}
