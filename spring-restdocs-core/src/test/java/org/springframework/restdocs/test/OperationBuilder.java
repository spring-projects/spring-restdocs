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

package org.springframework.restdocs.test;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.runners.model.Statement;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.mustache.Mustache;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationRequestPartFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.operation.RequestCookie;
import org.springframework.restdocs.operation.StandardOperation;
import org.springframework.restdocs.snippet.RestDocumentationContextPlaceholderResolverFactory;
import org.springframework.restdocs.snippet.StandardWriterResolver;
import org.springframework.restdocs.snippet.WriterResolver;
import org.springframework.restdocs.templates.StandardTemplateResourceResolver;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.mustache.AsciidoctorTableCellContentLambda;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

/**
 * Basic builder API for creating an {@link Operation}.
 *
 * @author Andy Wilkinson
 */
public class OperationBuilder extends OperationTestRule {

	private final Map<String, Object> attributes = new HashMap<>();

	private OperationResponseBuilder responseBuilder;

	private String name;

	private File outputDirectory;

	private final TemplateFormat templateFormat;

	private OperationRequestBuilder requestBuilder;

	public OperationBuilder(TemplateFormat templateFormat) {
		this.templateFormat = templateFormat;
	}

	public OperationRequestBuilder request(String uri) {
		this.requestBuilder = new OperationRequestBuilder(uri);
		return this.requestBuilder;
	}

	public OperationResponseBuilder response() {
		this.responseBuilder = new OperationResponseBuilder();
		return this.responseBuilder;
	}

	public OperationBuilder attribute(String name, Object value) {
		this.attributes.put(name, value);
		return this;
	}

	private void prepare(String operationName, File outputDirectory) {
		this.name = operationName;
		this.outputDirectory = outputDirectory;
		this.requestBuilder = null;
		this.requestBuilder = null;
		this.attributes.clear();
	}

	public Operation build() {
		if (this.attributes.get(TemplateEngine.class.getName()) == null) {
			Map<String, Object> templateContext = new HashMap<>();
			templateContext.put("tableCellContent",
					new AsciidoctorTableCellContentLambda());
			this.attributes.put(TemplateEngine.class.getName(),
					new MustacheTemplateEngine(
							new StandardTemplateResourceResolver(this.templateFormat),
							Mustache.compiler().escapeHTML(false), templateContext));
		}
		RestDocumentationContext context = createContext();
		this.attributes.put(RestDocumentationContext.class.getName(), context);
		this.attributes.put(WriterResolver.class.getName(),
				new StandardWriterResolver(
						new RestDocumentationContextPlaceholderResolverFactory(), "UTF-8",
						this.templateFormat));
		return new StandardOperation(this.name,
				(this.requestBuilder == null
						? new OperationRequestBuilder("http://localhost/").buildRequest()
						: this.requestBuilder.buildRequest()),
				this.responseBuilder == null
						? new OperationResponseBuilder().buildResponse()
						: this.responseBuilder.buildResponse(),
				this.attributes);
	}

	private RestDocumentationContext createContext() {
		ManualRestDocumentation manualRestDocumentation = new ManualRestDocumentation(
				this.outputDirectory.getAbsolutePath());
		manualRestDocumentation.beforeTest(null, null);
		RestDocumentationContext context = manualRestDocumentation.beforeOperation();
		return context;
	}

	@Override
	public Statement apply(Statement base, File outputDirectory, String operationName) {
		prepare(operationName, outputDirectory);
		return base;
	}

	/**
	 * Basic builder API for creating an {@link OperationRequest}.
	 */
	public final class OperationRequestBuilder {

		private URI requestUri = URI.create("http://localhost/");

		private HttpMethod method = HttpMethod.GET;

		private byte[] content = new byte[0];

		private HttpHeaders headers = new HttpHeaders();

		private Parameters parameters = new Parameters();

		private List<OperationRequestPartBuilder> partBuilders = new ArrayList<>();

		private Collection<RequestCookie> cookies = new ArrayList<>();

		private OperationRequestBuilder(String uri) {
			this.requestUri = URI.create(uri);
		}

		private OperationRequest buildRequest() {
			List<OperationRequestPart> parts = new ArrayList<>();
			for (OperationRequestPartBuilder builder : this.partBuilders) {
				parts.add(builder.buildPart());
			}
			return new OperationRequestFactory().create(this.requestUri, this.method,
					this.content, this.headers, this.parameters, parts, this.cookies);
		}

		public Operation build() {
			return OperationBuilder.this.build();
		}

		public OperationRequestBuilder method(String method) {
			this.method = HttpMethod.valueOf(method);
			return this;
		}

		public OperationRequestBuilder content(String content) {
			this.content = content.getBytes();
			return this;
		}

		public OperationRequestBuilder content(byte[] content) {
			this.content = content;
			return this;
		}

		public OperationRequestBuilder param(String name, String... values) {
			if (values.length > 0) {
				for (String value : values) {
					this.parameters.add(name, value);
				}
			}
			else {
				this.parameters.put(name, Collections.<String>emptyList());
			}
			return this;
		}

		public OperationRequestBuilder header(String name, String value) {
			this.headers.add(name, value);
			return this;
		}

		public OperationRequestPartBuilder part(String name, byte[] content) {
			OperationRequestPartBuilder partBuilder = new OperationRequestPartBuilder(
					name, content);
			this.partBuilders.add(partBuilder);
			return partBuilder;
		}

		public OperationRequestBuilder cookie(String name, String value) {
			this.cookies.add(new RequestCookie(name, value));
			return this;
		}

		/**
		 * Basic builder API for creating an {@link OperationRequestPart}.
		 */
		public final class OperationRequestPartBuilder {

			private final String name;

			private final byte[] content;

			private String submittedFileName;

			private HttpHeaders headers = new HttpHeaders();

			private OperationRequestPartBuilder(String name, byte[] content) {
				this.name = name;
				this.content = content;
			}

			public OperationRequestPartBuilder submittedFileName(
					String submittedFileName) {
				this.submittedFileName = submittedFileName;
				return this;
			}

			public OperationRequestBuilder and() {
				return OperationRequestBuilder.this;
			}

			public Operation build() {
				return OperationBuilder.this.build();
			}

			private OperationRequestPart buildPart() {
				return new OperationRequestPartFactory().create(this.name,
						this.submittedFileName, this.content, this.headers);
			}

			public OperationRequestPartBuilder header(String name, String value) {
				this.headers.add(name, value);
				return this;
			}
		}
	}

	/**
	 * Basic builder API for creating an {@link OperationResponse}.
	 */
	public final class OperationResponseBuilder {

		private HttpStatus status = HttpStatus.OK;

		private HttpHeaders headers = new HttpHeaders();

		private byte[] content = new byte[0];

		private OperationResponse buildResponse() {
			return new OperationResponseFactory().create(this.status, this.headers,
					this.content);
		}

		public OperationResponseBuilder status(int status) {
			this.status = HttpStatus.valueOf(status);
			return this;
		}

		public OperationResponseBuilder header(String name, String value) {
			this.headers.add(name, value);
			return this;
		}

		public OperationResponseBuilder content(byte[] content) {
			this.content = content;
			return this;
		}

		public OperationResponseBuilder content(String content) {
			this.content = content.getBytes();
			return this;
		}

		public Operation build() {
			return OperationBuilder.this.build();
		}

	}

}
