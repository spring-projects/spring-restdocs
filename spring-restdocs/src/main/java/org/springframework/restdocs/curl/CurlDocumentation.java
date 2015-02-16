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

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.snippet.DocumentationWriter;
import org.springframework.restdocs.snippet.DocumentationWriter.DocumentationAction;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Static factory methods for documenting a RESTful API as if it were being driven using
 * the cURL command-line utility.
 * 
 * @author Andy Wilkinson
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
					throws Exception {
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
					throws Exception {
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
					throws Exception {
				writer.shellCommand(new CurlRequestDocumentationAction(writer, result,
						getCurlConfiguration()));
				writer.codeBlock("http", new CurlResponseDocumentationAction(writer,
						result, getCurlConfiguration()));
			}
		};
	}

	private static final class CurlRequestDocumentationAction implements
			DocumentationAction {

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
		public void perform() throws Exception {
			MockHttpServletRequest request = this.result.getRequest();
			this.writer.print(String.format("curl %s://%s:%d%s", request.getScheme(),
					request.getRemoteHost(), request.getRemotePort(),
					request.getRequestURI()));

			if (this.curlConfiguration.isIncludeResponseHeaders()) {
				this.writer.print(" -i");
			}

			RequestMethod requestMethod = RequestMethod.valueOf(request.getMethod());
			if (requestMethod != RequestMethod.GET) {
				this.writer.print(String.format(" -X %s", requestMethod.toString()));
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

			this.writer.println();
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
		public void perform() throws Exception {
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
