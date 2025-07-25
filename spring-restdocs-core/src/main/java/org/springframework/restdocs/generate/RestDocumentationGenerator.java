/*
 * Copyright 2014-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.generate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.RequestConverter;
import org.springframework.restdocs.operation.ResponseConverter;
import org.springframework.restdocs.operation.StandardOperation;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.util.Assert;

/**
 * A {@code RestDocumentationGenerator} is used to generate documentation snippets from
 * the request and response of an operation performed on a service.
 *
 * @param <REQ> the request type that can be handled
 * @param <RESP> the response type that can be handled
 * @author Andy Wilkinson
 * @author Filip Hrisafov
 * @since 1.1.0
 */
public final class RestDocumentationGenerator<REQ, RESP> {

	/**
	 * Name of the operation attribute used to hold the request's URL template.
	 */
	public static final String ATTRIBUTE_NAME_URL_TEMPLATE = "org.springframework.restdocs.urlTemplate";

	/**
	 * Name of the operation attribute used to hold the {@link List} of default snippets.
	 */
	public static final String ATTRIBUTE_NAME_DEFAULT_SNIPPETS = "org.springframework.restdocs.defaultSnippets";

	/**
	 * Name of the operation attribute used to hold the default operation request
	 * preprocessor.
	 */
	public static final String ATTRIBUTE_NAME_DEFAULT_OPERATION_REQUEST_PREPROCESSOR = "org.springframework.restdocs.defaultOperationRequestPreprocessor";

	/**
	 * Name of the operation attribute used to hold the default operation response
	 * preprocessor.
	 */
	public static final String ATTRIBUTE_NAME_DEFAULT_OPERATION_RESPONSE_PREPROCESSOR = "org.springframework.restdocs.defaultOperationResponsePreprocessor";

	private final String identifier;

	private final OperationRequestPreprocessor requestPreprocessor;

	private final OperationResponsePreprocessor responsePreprocessor;

	private final List<Snippet> snippets;

	private final RequestConverter<REQ> requestConverter;

	private final ResponseConverter<RESP> responseConverter;

	/**
	 * Creates a new {@code RestDocumentationGenerator} for the operation identified by
	 * the given {@code identifier}. The given {@code requestConverter} and
	 * {@code responseConverter} are used to convert the operation's request and response
	 * into generic {@code OperationRequest} and {@code OperationResponse} instances that
	 * can then be documented. The given documentation {@code snippets} will be produced.
	 * @param identifier the identifier for the operation
	 * @param requestConverter the request converter
	 * @param responseConverter the response converter
	 * @param snippets the snippets
	 */
	public RestDocumentationGenerator(String identifier, RequestConverter<REQ> requestConverter,
			ResponseConverter<RESP> responseConverter, Snippet... snippets) {
		this(identifier, requestConverter, responseConverter, new IdentityOperationRequestPreprocessor(),
				new IdentityOperationResponsePreprocessor(), snippets);
	}

	/**
	 * Creates a new {@code RestDocumentationGenerator} for the operation identified by
	 * the given {@code identifier}. The given {@code requestConverter} and
	 * {@code responseConverter} are used to convert the operation's request and response
	 * into generic {@code OperationRequest} and {@code OperationResponse} instances that
	 * can then be documented. The given {@code requestPreprocessor} is applied to the
	 * request before it is documented. The given documentation {@code snippets} will be
	 * produced.
	 * @param identifier the identifier for the operation
	 * @param requestConverter the request converter
	 * @param responseConverter the response converter
	 * @param requestPreprocessor the request preprocessor
	 * @param snippets the snippets
	 */
	public RestDocumentationGenerator(String identifier, RequestConverter<REQ> requestConverter,
			ResponseConverter<RESP> responseConverter, OperationRequestPreprocessor requestPreprocessor,
			Snippet... snippets) {
		this(identifier, requestConverter, responseConverter, requestPreprocessor,
				new IdentityOperationResponsePreprocessor(), snippets);
	}

	/**
	 * Creates a new {@code RestDocumentationGenerator} for the operation identified by
	 * the given {@code identifier}. The given {@code requestConverter} and
	 * {@code responseConverter} are used to convert the operation's request and response
	 * into generic {@code OperationRequest} and {@code OperationResponse} instances that
	 * can then be documented. The given {@code responsePreprocessor} is applied to the
	 * response before it is documented. The given documentation {@code snippets} will be
	 * produced.
	 * @param identifier the identifier for the operation
	 * @param requestConverter the request converter
	 * @param responseConverter the response converter
	 * @param responsePreprocessor the response preprocessor
	 * @param snippets the snippets
	 */
	public RestDocumentationGenerator(String identifier, RequestConverter<REQ> requestConverter,
			ResponseConverter<RESP> responseConverter, OperationResponsePreprocessor responsePreprocessor,
			Snippet... snippets) {
		this(identifier, requestConverter, responseConverter, new IdentityOperationRequestPreprocessor(),
				responsePreprocessor, snippets);
	}

	/**
	 * Creates a new {@code RestDocumentationGenerator} for the operation identified by
	 * the given {@code identifier}. The given {@code requestConverter} and
	 * {@code responseConverter} are used to convert the operation's request and response
	 * into generic {@code OperationRequest} and {@code OperationResponse} instances that
	 * can then be documented. The given {@code requestPreprocessor} and
	 * {@code responsePreprocessor} are applied to the request and response before they
	 * are documented. The given documentation {@code snippets} will be produced.
	 * @param identifier the identifier for the operation
	 * @param requestConverter the request converter
	 * @param responseConverter the response converter
	 * @param requestPreprocessor the request preprocessor
	 * @param responsePreprocessor the response preprocessor
	 * @param snippets the snippets
	 */
	public RestDocumentationGenerator(String identifier, RequestConverter<REQ> requestConverter,
			ResponseConverter<RESP> responseConverter, OperationRequestPreprocessor requestPreprocessor,
			OperationResponsePreprocessor responsePreprocessor, Snippet... snippets) {
		Assert.notNull(identifier, "identifier must be non-null");
		Assert.notNull(requestConverter, "requestConverter must be non-null");
		Assert.notNull(responseConverter, "responseConverter must be non-null");
		Assert.notNull(identifier, "identifier must be non-null");
		Assert.notNull(requestPreprocessor, "requestPreprocessor must be non-null");
		Assert.notNull(responsePreprocessor, "responsePreprocessor must be non-null");
		Assert.notNull(snippets, "snippets must be non-null");
		this.identifier = identifier;
		this.requestConverter = requestConverter;
		this.responseConverter = responseConverter;
		this.requestPreprocessor = requestPreprocessor;
		this.responsePreprocessor = responsePreprocessor;
		this.snippets = new ArrayList<>(Arrays.asList(snippets));
	}

	/**
	 * Handles the given {@code request} and {@code response}, producing documentation
	 * snippets for them using the given {@code configuration}.
	 * @param request the request
	 * @param response the request
	 * @param configuration the configuration
	 * @throws RestDocumentationGenerationException if a failure occurs during handling
	 */
	public void handle(REQ request, RESP response, Map<String, Object> configuration) {
		Map<String, Object> attributes = new HashMap<>(configuration);
		OperationRequest operationRequest = preprocessRequest(this.requestConverter.convert(request), attributes);
		OperationResponse operationResponse = preprocessResponse(this.responseConverter.convert(response), attributes);
		Operation operation = new StandardOperation(this.identifier, operationRequest, operationResponse, attributes);
		try {
			for (Snippet snippet : getSnippets(attributes)) {
				snippet.document(operation);
			}
		}
		catch (IOException ex) {
			throw new RestDocumentationGenerationException(ex);
		}
	}

	/**
	 * Creates a new {@link RestDocumentationGenerator} with the same configuration as
	 * this one other than its snippets. The new generator will use the given
	 * {@code snippets}.
	 * @param snippets the snippets
	 * @return the new generator
	 */
	public RestDocumentationGenerator<REQ, RESP> withSnippets(Snippet... snippets) {
		return new RestDocumentationGenerator<>(this.identifier, this.requestConverter, this.responseConverter,
				this.requestPreprocessor, this.responsePreprocessor, snippets);
	}

	@SuppressWarnings("unchecked")
	private List<Snippet> getSnippets(Map<String, Object> configuration) {
		List<Snippet> combinedSnippets = new ArrayList<>();
		List<Snippet> defaultSnippets = (List<Snippet>) configuration.get(ATTRIBUTE_NAME_DEFAULT_SNIPPETS);
		if (defaultSnippets != null) {
			combinedSnippets.addAll(defaultSnippets);
		}
		combinedSnippets.addAll(this.snippets);
		return combinedSnippets;
	}

	private OperationRequest preprocessRequest(OperationRequest request, Map<String, Object> configuration) {
		return preprocess(getRequestPreprocessors(configuration), request, this::preprocess);
	}

	private OperationRequest preprocess(OperationRequestPreprocessor preprocessor, OperationRequest request) {
		return preprocessor.preprocess(request);
	}

	private List<OperationRequestPreprocessor> getRequestPreprocessors(Map<String, Object> configuration) {
		return getPreprocessors(this.requestPreprocessor,
				RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_OPERATION_REQUEST_PREPROCESSOR, configuration);
	}

	private OperationResponse preprocessResponse(OperationResponse response, Map<String, Object> configuration) {
		return preprocess(getResponsePreprocessors(configuration), response, this::preprocess);
	}

	private OperationResponse preprocess(OperationResponsePreprocessor preprocessor, OperationResponse response) {
		return preprocessor.preprocess(response);
	}

	private List<OperationResponsePreprocessor> getResponsePreprocessors(Map<String, Object> configuration) {
		return getPreprocessors(this.responsePreprocessor,
				RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_OPERATION_RESPONSE_PREPROCESSOR, configuration);
	}

	private <P, T> T preprocess(List<P> preprocessors, T target, BiFunction<P, T, T> function) {
		T processed = target;
		for (P preprocessor : preprocessors) {
			processed = function.apply(preprocessor, processed);
		}
		return processed;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getPreprocessors(T preprocessor, String preprocessorAttribute,
			Map<String, Object> configuration) {
		List<T> preprocessors = new ArrayList<>(2);
		preprocessors.add(preprocessor);
		T defaultResponsePreprocessor = (T) configuration.get(preprocessorAttribute);
		if (defaultResponsePreprocessor != null) {
			preprocessors.add(defaultResponsePreprocessor);
		}
		return preprocessors;
	}

	private static final class IdentityOperationRequestPreprocessor implements OperationRequestPreprocessor {

		@Override
		public OperationRequest preprocess(OperationRequest request) {
			return request;
		}

	}

	private static final class IdentityOperationResponsePreprocessor implements OperationResponsePreprocessor {

		@Override
		public OperationResponse preprocess(OperationResponse response) {
			return response;
		}

	}

}
