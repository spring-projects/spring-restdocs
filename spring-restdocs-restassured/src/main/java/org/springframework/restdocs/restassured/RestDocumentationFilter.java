/*
 * Copyright 2014-2016 the original author or authors.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.config.SnippetConfigurer;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.StandardOperation;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.snippet.Snippet;

import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.FilterContext;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.FilterableResponseSpecification;

/**
 * A REST Assured {@link Filter} for documenting RESTful APIs.
 *
 * @author Andy Wilkinson
 */
public final class RestDocumentationFilter implements Filter {

	private final String identifier;

	private final OperationRequestPreprocessor requestPreprocessor;

	private final OperationResponsePreprocessor responsePreprocessor;

	private final List<Snippet> snippets;

	RestDocumentationFilter(String identifier, Snippet... snippets) {
		this(identifier, new IdentityOperationRequestPreprocessor(),
				new IdentityOperationResponsePreprocessor(), snippets);
	}

	RestDocumentationFilter(String identifier,
			OperationRequestPreprocessor operationRequestPreprocessor,
			Snippet... snippets) {
		this(identifier, operationRequestPreprocessor,
				new IdentityOperationResponsePreprocessor(), snippets);
	}

	RestDocumentationFilter(String identifier,
			OperationResponsePreprocessor operationResponsePreprocessor,
			Snippet... snippets) {
		this(identifier, new IdentityOperationRequestPreprocessor(),
				operationResponsePreprocessor, snippets);
	}

	RestDocumentationFilter(String identifier,
			OperationRequestPreprocessor requestPreprocessor,
			OperationResponsePreprocessor responsePreprocessor, Snippet... snippets) {
		this.identifier = identifier;
		this.requestPreprocessor = requestPreprocessor;
		this.responsePreprocessor = responsePreprocessor;
		this.snippets = new ArrayList<>(Arrays.asList(snippets));
	}

	@Override
	public Response filter(FilterableRequestSpecification requestSpec,
			FilterableResponseSpecification responseSpec, FilterContext context) {
		Response response = context.next(requestSpec, responseSpec);

		OperationRequest operationRequest = this.requestPreprocessor
				.preprocess(new RestAssuredOperationRequestFactory()
						.createOperationRequest(requestSpec));
		OperationResponse operationResponse = this.responsePreprocessor
				.preprocess(new RestAssuredOperationResponseFactory()
						.createOperationResponse(response));

		RestDocumentationContext documentationContext = context
				.getValue(RestDocumentationContext.class.getName());

		Map<String, Object> attributes = new HashMap<>();
		attributes.put(RestDocumentationContext.class.getName(), documentationContext);
		attributes.put("org.springframework.restdocs.urlTemplate",
				requestSpec.getUserDefinedPath());
		Map<String, Object> configuration = context
				.getValue("org.springframework.restdocs.configuration");
		attributes.putAll(configuration);

		Operation operation = new StandardOperation(this.identifier, operationRequest,
				operationResponse, attributes);

		try {
			for (Snippet snippet : getSnippets(configuration)) {
				snippet.document(operation);
			}
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return response;
	}

	/**
	 * Adds the given {@code snippets} such that they are documented when this result
	 * handler is called.
	 *
	 * @param snippets the snippets to add
	 * @return this {@code RestDocumentationFilter}
	 */
	public RestDocumentationFilter snippets(Snippet... snippets) {
		this.snippets.addAll(Arrays.asList(snippets));
		return this;
	}

	@SuppressWarnings("unchecked")
	private List<Snippet> getSnippets(Map<String, Object> configuration) {
		List<Snippet> combinedSnippets = new ArrayList<>(
				(List<Snippet>) configuration
						.get(SnippetConfigurer.ATTRIBUTE_DEFAULT_SNIPPETS));
		combinedSnippets.addAll(this.snippets);
		return combinedSnippets;
	}

	private static final class IdentityOperationRequestPreprocessor implements
			OperationRequestPreprocessor {

		@Override
		public OperationRequest preprocess(OperationRequest request) {
			return request;
		}

	}

	private static final class IdentityOperationResponsePreprocessor implements
			OperationResponsePreprocessor {

		@Override
		public OperationResponse preprocess(OperationResponse response) {
			return response;
		}

	}

}
