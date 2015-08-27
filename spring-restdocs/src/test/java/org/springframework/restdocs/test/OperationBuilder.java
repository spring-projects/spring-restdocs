package org.springframework.restdocs.test;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.config.RestDocumentationContext;
import org.springframework.restdocs.config.RestDocumentationContextPlaceholderResolver;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.operation.StandardOperation;
import org.springframework.restdocs.operation.StandardOperationRequest;
import org.springframework.restdocs.operation.StandardOperationRequestPart;
import org.springframework.restdocs.operation.StandardOperationResponse;
import org.springframework.restdocs.snippet.StandardWriterResolver;
import org.springframework.restdocs.snippet.WriterResolver;
import org.springframework.restdocs.templates.StandardTemplateResourceResolver;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

public class OperationBuilder {

	private final Map<String, Object> attributes = new HashMap<>();

	private final OperationResponseBuilder responseBuilder = new OperationResponseBuilder();

	private final String name;

	private OperationRequestBuilder requestBuilder;

	public OperationBuilder(String name) {
		this.name = name;
	}

	public OperationRequestBuilder request(String uri) {
		this.requestBuilder = new OperationRequestBuilder(uri);
		return this.requestBuilder;
	}

	public OperationResponseBuilder response() {
		return this.responseBuilder;
	}

	public OperationBuilder attribute(String name, Object value) {
		this.attributes.put(name, value);
		return this;
	}

	public Operation build() {
		if (this.attributes.get(TemplateEngine.class.getName()) == null) {
			this.attributes.put(TemplateEngine.class.getName(),
					new MustacheTemplateEngine(new StandardTemplateResourceResolver()));
		}
		RestDocumentationContext context = new RestDocumentationContext(null);
		this.attributes.put(RestDocumentationContext.class.getName(), context);
		this.attributes.put(WriterResolver.class.getName(), new StandardWriterResolver(
				new RestDocumentationContextPlaceholderResolver(context)));
		return new StandardOperation(this.name,
				(this.requestBuilder == null ? new OperationRequestBuilder(
						"http://localhost/").buildRequest() : this.requestBuilder
						.buildRequest()),
				this.responseBuilder.buildResponse(), this.attributes);
	}

	public class OperationRequestBuilder {

		private URI requestUri = URI.create("http://localhost/");

		private HttpMethod method = HttpMethod.GET;

		private byte[] content = new byte[0];

		private HttpHeaders headers = new HttpHeaders();

		private Parameters parameters = new Parameters();

		private List<OperationRequestPartBuilder> partBuilders = new ArrayList<>();

		public OperationRequestBuilder(String uri) {
			this.requestUri = URI.create(uri);
		}

		private OperationRequest buildRequest() {
			List<OperationRequestPart> parts = new ArrayList<>();
			for (OperationRequestPartBuilder builder : this.partBuilders) {
				parts.add(builder.buildPart());
			}
			return new StandardOperationRequest(this.requestUri, this.method,
					this.content, this.headers, this.parameters, parts);
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

		public OperationRequestBuilder param(String name, String... values) {
			for (String value : values) {
				this.parameters.add(name, value);
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

		public class OperationRequestPartBuilder {

			private final String name;

			private final byte[] content;

			private String submittedFileName;

			private HttpHeaders headers = new HttpHeaders();

			private OperationRequestPartBuilder(String name, byte[] content) {
				this.name = name;
				this.content = content;
			}

			public OperationRequestPartBuilder submittedFileName(String submittedFileName) {
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
				return new StandardOperationRequestPart(this.name,
						this.submittedFileName, this.content, this.headers);
			}

			public OperationRequestPartBuilder header(String name, String value) {
				this.headers.add(name, value);
				return this;
			}
		}
	}

	public class OperationResponseBuilder {

		private HttpStatus status = HttpStatus.OK;

		private HttpHeaders headers = new HttpHeaders();

		private byte[] content = new byte[0];

		private OperationResponse buildResponse() {
			return new StandardOperationResponse(this.status, this.headers, this.content);
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
