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

package org.springframework.restdocs.core;

import static org.junit.Assert.fail;
import static org.springframework.restdocs.core.IterableEnumeration.iterable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.*;
import java.util.Map.Entry;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.core.DocumentationWriter.DocumentationAction;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMethod;

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
				writer.codeBlock("http", new CurlResponseDocumentationAction(writer,
						result, getCurlConfiguration()));
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
				writer.codeBlock("http", new CurlResponseDocumentationAction(writer,
						result, getCurlConfiguration()));
			}
		};
	}

	/**
	 * package visibility for tests
	 */
	static final class CurlRequestDocumentationAction implements
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
			this.writer.print(String.format("curl %s://%s:%d%s%s", request.getScheme(),
					request.getRemoteHost(), request.getRemotePort(),
					request.getRequestURI(), queryParamsToString(request)));

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

		/**
		 * returns query parameter representation in request e.g<br>
		 * <code>?firstParam=firstValue&secondParam&thirdParam=thirdValue</code>
		 * <br><br>
		 * package visibility for tests
		 */
		String queryParamsToString(MockHttpServletRequest request) {

			Enumeration<String> parameterNames = request.getParameterNames();

			if(!parameterNames.hasMoreElements()){
				return "";
			}

			StringBuilder sb = new StringBuilder("?");

			while (parameterNames.hasMoreElements()){

				String name = parameterNames.nextElement();

				sb.append(name);

				Object value = request.getParameter(name);
				if(value != null){
					sb.append("=");
					sb.append(value);
				}

				if(parameterNames.hasMoreElements()){
					sb.append("&");
				}

			}

			return sb.toString();

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

		protected PrintStream createPrintStream() throws FileNotFoundException {

			File outputFile = new File(this.outputDir, this.fileName + ".asciidoc");
			if (!outputFile.isAbsolute()) {
				outputFile = makeAbsolute(outputFile);
			}

			if (outputFile != null) {
				outputFile.getParentFile().mkdirs();
				return new PrintStream(new FileOutputStream(outputFile));
			}

			return System.out;
		}

		private static File makeAbsolute(File outputFile) {
			File outputDir = new DocumentationProperties().getOutputDir();
			if (outputDir != null) {
				return new File(outputDir, outputFile.getPath());
			}
			return null;
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

		private final Map<String, LinkDescriptor> descriptorsByRel = new HashMap<String, LinkDescriptor>();

		private final LinkExtractor extractor;

		public LinkDocumentingResultHandler(String outputDir,
				LinkExtractor linkExtractor, List<LinkDescriptor> descriptors) {
			super(outputDir, "links");
			this.extractor = linkExtractor;
			for (LinkDescriptor descriptor : descriptors) {
				Assert.hasText(descriptor.getRel());
				Assert.hasText(descriptor.getDescription());
				this.descriptorsByRel.put(descriptor.getRel(), descriptor);
			}
		}

		@Override
		void handle(MvcResult result, DocumentationWriter writer) throws Exception {
			Map<String, List<Link>> links;
			if (this.extractor != null) {
				links = this.extractor.extractLinks(result.getResponse());
			}
			else {
				String contentType = result.getResponse().getContentType();
				LinkExtractor extractorForContentType = LinkExtractors
						.extractorForContentType(contentType);
				if (extractorForContentType != null) {
					links = extractorForContentType.extractLinks(result.getResponse());
				}
				else {
					throw new IllegalStateException(
							"No LinkExtractor has been provided and one is not available for the content type "
									+ contentType);
				}

			}

			Set<String> actualRels = links.keySet();
			Set<String> expectedRels = this.descriptorsByRel.keySet();

			Set<String> undocumentedRels = new HashSet<String>(actualRels);
			undocumentedRels.removeAll(expectedRels);

			Set<String> missingRels = new HashSet<String>(expectedRels);
			missingRels.removeAll(actualRels);

			if (!undocumentedRels.isEmpty() || !missingRels.isEmpty()) {
				String message = "";
				if (!undocumentedRels.isEmpty()) {
					message += "Links with the following relations were not documented: "
							+ undocumentedRels;
				}
				if (!missingRels.isEmpty()) {
					message += "Links with the following relations were not found in the response: "
							+ missingRels;
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
