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
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.core.DocumentationWriter.DocumentationAction;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class RestDocumentationResultHandlers {

	public static CurlResultHandler documentCurlRequest(String outputDir) {
		return new CurlResultHandler(outputDir, "request") {
			@Override
			public void handle(MvcResult result, DocumentationWriter writer)
					throws Exception {
				writer.shellCommand(new CurlRequestDocumentationAction(writer, result,
						getCurlConfiguration()));
			}
		};
	}

	public static CurlResultHandler documentCurlResponse(String outputDir) {
		return new CurlResultHandler(outputDir, "response") {
			@Override
			public void handle(MvcResult result, DocumentationWriter writer)
					throws Exception {
				writer.codeBlock("http", new CurlResponseDocumentationAction(writer, result,
						getCurlConfiguration()));
			}
		};
	}

	public static CurlResultHandler documentCurlRequestAndResponse(String outputDir) {
		return new CurlResultHandler(outputDir, "request-response") {
			@Override
			public void handle(MvcResult result, DocumentationWriter writer)
					throws Exception {
				writer.shellCommand(new CurlRequestDocumentationAction(writer, result,
						getCurlConfiguration()));
				writer.codeBlock("http", new CurlResponseDocumentationAction(writer, result,
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

	public static abstract class RestDocumentationResultHandler implements ResultHandler {
		
		private String outputDir;
		
		private String fileName;

		public RestDocumentationResultHandler(String outputDir, String fileName) {
			this.outputDir = outputDir;
			this.fileName = fileName;
		}
		
		abstract void handle(MvcResult result, DocumentationWriter writer)
				throws Exception;

		@Override
		public void handle(MvcResult result) throws Exception {
			PrintStream printStream = createPrintStream();
			try {
				handle(result, new DocumentationWriter(printStream));
			}
			finally {
				printStream.close();
			}
		}

		protected PrintStream createPrintStream()
				throws FileNotFoundException {
			
			File outputFile = new File(this.outputDir, this.fileName + ".asciidoc");
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
	}

	public static abstract class CurlResultHandler extends RestDocumentationResultHandler {

		private final CurlConfiguration curlConfiguration = new CurlConfiguration();

		public CurlResultHandler(String outputDir, String fileName) {
			super(outputDir, fileName);
		}

		CurlConfiguration getCurlConfiguration() {
			return this.curlConfiguration;
		}

		public CurlResultHandler includeResponseHeaders() {
			this.curlConfiguration.includeResponseHeaders = true;
			return this;
		}
	}

	static class LinkDocumentingResultHandler extends RestDocumentationResultHandler {

		private final ObjectMapper objectMapper = new ObjectMapper();

		private final Map<String, LinkDescriptor> descriptorsByRel = new HashMap<String, LinkDescriptor>();

		private final LinkExtractor extractor;

		public LinkDocumentingResultHandler(String outputDir, LinkExtractor linkExtractor, List<LinkDescriptor> descriptors) {
			super(outputDir, "links");
			this.extractor = linkExtractor;
			for (LinkDescriptor descriptor: descriptors) {
				Assert.hasText(descriptor.getRel());
				Assert.hasText(descriptor.getDescription());
				this.descriptorsByRel.put(descriptor.getRel(), descriptor);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		void handle(MvcResult result, DocumentationWriter writer) throws Exception {
			Map<String, Object> json = this.objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
			Map<String, Object> links = this.extractor.extractLinks(json);

			Set<String> actualRels = links.keySet();
			Set<String> expectedRels = this.descriptorsByRel.keySet();

			Set<String> undocumentedRels = new HashSet<String>(actualRels);
			undocumentedRels.removeAll(expectedRels);

			Set<String> missingRels = new HashSet<String>(expectedRels);
			missingRels.removeAll(actualRels);

			if (!undocumentedRels.isEmpty() || !missingRels.isEmpty()) {
				String message = "";
				if (!undocumentedRels.isEmpty()) {
					message += "Links with the following relations were not documented: " + undocumentedRels;
				}
				if (!missingRels.isEmpty()) {
					message += "Links with the following relations were not found in the response: " + missingRels;
				}
				fail(message);
			}

			Assert.isTrue(actualRels.equals(expectedRels));

			writer.println("|===");
			writer.println("| Relation | Description");

			for (Entry<String, LinkDescriptor> entry : this.descriptorsByRel.entrySet()) {
				writer.println();
				writer.println("| " + entry.getKey());
				writer.println("| " + entry.getValue().getDescription());
			}

			writer.println("|===");
		}

	}
}
