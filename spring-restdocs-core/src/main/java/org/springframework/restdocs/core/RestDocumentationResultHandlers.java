/*
 * Copyright 2014 the original author or authors.
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

package org.springframework.restdocs.core;

import static org.springframework.restdocs.core.IterableEnumeration.iterable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.core.DocumentationWriter.DocumentationAction;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class RestDocumentationResultHandlers {

	public static CurlResultHandler documentCurlRequest() {
		return new CurlResultHandler("Request.asciidoc") {
			@Override
			public void handle(MvcResult result, DocumentationWriter writer)
					throws Exception {
				writer.shellCommand(new CurlRequestDocumentationAction(writer, result,
						getCurlConfiguration()));
			}
		};
	}

	public static CurlResultHandler documentCurlResponse() {
		return new CurlResultHandler("Response.asciidoc") {
			@Override
			public void handle(MvcResult result, DocumentationWriter writer)
					throws Exception {
				writer.codeBlock(new CurlResponseDocumentationAction(writer, result,
						getCurlConfiguration()));
			}
		};
	}

	public static CurlResultHandler documentCurlRequestAndResponse() {
		return new CurlResultHandler("RequestResponse.asciidoc") {
			@Override
			public void handle(MvcResult result, DocumentationWriter writer)
					throws Exception {
				writer.shellCommand(new CurlRequestDocumentationAction(writer, result,
						getCurlConfiguration()));
				writer.codeBlock(new CurlResponseDocumentationAction(writer, result,
						getCurlConfiguration()));
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

			if (this.curlConfiguration.includeResponseHeaders) {
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
			StringWriter writer = new StringWriter();
			FileCopyUtils.copy(request.getReader(), writer);
			return writer.toString();
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
			if (this.curlConfiguration.includeResponseHeaders) {
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

	private static class CurlConfiguration {

		private boolean includeResponseHeaders = false;

	}

	public static abstract class CurlResultHandler implements ResultHandler {

		private final CurlConfiguration curlConfiguration = new CurlConfiguration();
		
		private String suffix;
		
		public CurlResultHandler(String suffix) {
			this.suffix = suffix;
		}

		CurlConfiguration getCurlConfiguration() {
			return this.curlConfiguration;
		}

		public CurlResultHandler includeResponseHeaders() {
			this.curlConfiguration.includeResponseHeaders = true;
			return this;
		}

		@Override
		public void handle(MvcResult result) throws Exception {
			PrintStream printStream = createPrintStream(this.suffix);
			try {
				handle(result, new DocumentationWriter(printStream));
			}
			finally {
				printStream.close();
			}
		}
		
		private PrintStream createPrintStream(String suffix)
				throws FileNotFoundException {
			DocumentationContext context = DocumentationContext.current();
			if (context == null) {
				throw new IllegalStateException();
			}

			String path = resolveOutputPath(context);

			File outputFile = new File(path);
			if (!outputFile.isAbsolute()) {
				outputFile = makeAbsolute(outputFile);
			}
			outputFile.getParentFile().mkdirs();

			return new PrintStream(new FileOutputStream(outputFile));
		}

		private static File makeAbsolute(File outputFile) {
			return new File(new DocumentationProperties().getOutputDir(),
					outputFile.getPath());
		}

		private String resolveOutputPath(DocumentationContext context) {
			String shortClassName = getShortClassName(context.getDocumentationClass());
			return shortClassName + "/" + context.getDocumentationMethod().getName() + this.suffix;
		}

		private String getShortClassName(Class<?> clazz) {
			int index = clazz.getName().lastIndexOf('.');
			if (index >= 0) {
				return clazz.getName().substring(index + 1);
			}
			return clazz.getName();
		}

		abstract void handle(MvcResult result, DocumentationWriter writer)
				throws Exception;

	}
}
