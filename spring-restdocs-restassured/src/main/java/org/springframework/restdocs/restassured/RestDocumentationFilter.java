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

package org.springframework.restdocs.restassured;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.StandardOperation;
import org.springframework.restdocs.snippet.RestDocumentationContextPlaceholderResolver;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.StandardWriterResolver;
import org.springframework.restdocs.snippet.WriterResolver;
import org.springframework.restdocs.templates.StandardTemplateResourceResolver;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.FilterContext;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.FilterableResponseSpecification;

public class RestDocumentationFilter implements Filter {

	private final RestDocumentation restDocumentation;

	private final String identifier;

	private final List<Snippet> snippets;

	public RestDocumentationFilter(RestDocumentation restDocumentation,
			String identifier, Snippet... snippets) {
		this.restDocumentation = restDocumentation;
		this.identifier = identifier;
		this.snippets = Arrays.asList(snippets);
	}

	@Override
	public Response filter(FilterableRequestSpecification requestSpec,
			FilterableResponseSpecification responseSpec, FilterContext context) {
		Response response = context.next(requestSpec, responseSpec);

		OperationRequest operationRequest = new RestAssuredOperationRequestFactory()
				.createOperationRequest(requestSpec, context);
		OperationResponse operationResponse = new RestAssuredOperationResponseFactory()
				.createOperationResponse(response);

		RestDocumentationContext documentationContext = this.restDocumentation
				.beforeOperation();

		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(WriterResolver.class.getName(), new StandardWriterResolver(
				new RestDocumentationContextPlaceholderResolver(documentationContext)));
		attributes.put(RestDocumentationContext.class.getName(), documentationContext);
		attributes.put(TemplateEngine.class.getName(), new MustacheTemplateEngine(
				new StandardTemplateResourceResolver()));

		Operation operation = new StandardOperation(this.identifier, operationRequest,
				operationResponse, attributes);

		try {
			for (Snippet snippet : this.snippets) {
				snippet.document(operation);
			}
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return response;
	}

}
