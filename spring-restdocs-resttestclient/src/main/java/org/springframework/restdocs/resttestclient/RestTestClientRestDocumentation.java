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

package org.springframework.restdocs.resttestclient;

import java.util.Map;
import java.util.function.Consumer;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.client.RestTestClient.BodyContentSpec;
import org.springframework.test.web.servlet.client.RestTestClient.BodySpec;
import org.springframework.test.web.servlet.client.RestTestClient.Builder;

/**
 * Static factory methods for documenting RESTful APIs using Spring Framework's
 * {@link RestTestClient}.
 *
 * @author Andy Wilkinson
 * @since 4.1.0
 */
public abstract class RestTestClientRestDocumentation {

	private static final RestTestClientRequestConverter REQUEST_CONVERTER = new RestTestClientRequestConverter();

	private static final RestTestClientResponseConverter RESPONSE_CONVERTER = new RestTestClientResponseConverter();

	private RestTestClientRestDocumentation() {

	}

	/**
	 * Provides access to a {@link ClientHttpRequestInterceptor} that can be used to
	 * configure a {@link RestTestClient} instance using the given
	 * {@code contextProvider}.
	 * @param contextProvider the context provider
	 * @return the configurer
	 * @see Builder#requestInterceptor(ClientHttpRequestInterceptor)
	 */
	public static RestTestClientRestDocumentationConfigurer documentationConfiguration(
			RestDocumentationContextProvider contextProvider) {
		return new RestTestClientRestDocumentationConfigurer(contextProvider);
	}

	/**
	 * Returns a {@link Consumer} that, when called, documents the API call with the given
	 * {@code identifier} using the given {@code snippets} in addition to any default
	 * snippets.
	 * @param identifier an identifier for the API call that is being documented
	 * @param snippets the snippets
	 * @param <T> the type of {@link ExchangeResult} that will be consumed
	 * @return the {@link Consumer} that will document the API call represented by the
	 * {@link ExchangeResult}.
	 * @see BodySpec#consumeWith(Consumer)
	 * @see BodyContentSpec#consumeWith(Consumer)
	 */
	public static <T extends ExchangeResult> Consumer<T> document(String identifier, Snippet... snippets) {
		return (result) -> new RestDocumentationGenerator<>(identifier, REQUEST_CONVERTER, RESPONSE_CONVERTER, snippets)
			.handle(result, result, retrieveConfiguration(result));
	}

	/**
	 * Documents the API call with the given {@code identifier} using the given
	 * {@code snippets} in addition to any default snippets. The given
	 * {@code requestPreprocessor} is applied to the request before it is documented.
	 * @param identifier an identifier for the API call that is being documented
	 * @param requestPreprocessor the request preprocessor
	 * @param snippets the snippets
	 * @param <T> the type of {@link ExchangeResult} that will be consumed
	 * @return the {@link Consumer} that will document the API call represented by the
	 * {@link ExchangeResult}.
	 */
	public static <T extends ExchangeResult> Consumer<T> document(String identifier,
			OperationRequestPreprocessor requestPreprocessor, Snippet... snippets) {
		return (result) -> new RestDocumentationGenerator<>(identifier, REQUEST_CONVERTER, RESPONSE_CONVERTER,
				requestPreprocessor, snippets)
			.handle(result, result, retrieveConfiguration(result));
	}

	/**
	 * Documents the API call with the given {@code identifier} using the given
	 * {@code snippets} in addition to any default snippets. The given
	 * {@code responsePreprocessor} is applied to the request before it is documented.
	 * @param identifier an identifier for the API call that is being documented
	 * @param responsePreprocessor the response preprocessor
	 * @param snippets the snippets
	 * @param <T> the type of {@link ExchangeResult} that will be consumed
	 * @return the {@link Consumer} that will document the API call represented by the
	 * {@link ExchangeResult}.
	 */
	public static <T extends ExchangeResult> Consumer<T> document(String identifier,
			OperationResponsePreprocessor responsePreprocessor, Snippet... snippets) {
		return (result) -> new RestDocumentationGenerator<>(identifier, REQUEST_CONVERTER, RESPONSE_CONVERTER,
				responsePreprocessor, snippets)
			.handle(result, result, retrieveConfiguration(result));
	}

	/**
	 * Documents the API call with the given {@code identifier} using the given
	 * {@code snippets} in addition to any default snippets. The given
	 * {@code requestPreprocessor} and {@code responsePreprocessor} are applied to the
	 * request and response respectively before they are documented.
	 * @param identifier an identifier for the API call that is being documented
	 * @param requestPreprocessor the request preprocessor
	 * @param responsePreprocessor the response preprocessor
	 * @param snippets the snippets
	 * @param <T> the type of {@link ExchangeResult} that will be consumed
	 * @return the {@link Consumer} that will document the API call represented by the
	 * {@link ExchangeResult}.
	 */
	public static <T extends ExchangeResult> Consumer<T> document(String identifier,
			OperationRequestPreprocessor requestPreprocessor, OperationResponsePreprocessor responsePreprocessor,
			Snippet... snippets) {
		return (result) -> new RestDocumentationGenerator<>(identifier, REQUEST_CONVERTER, RESPONSE_CONVERTER,
				requestPreprocessor, responsePreprocessor, snippets)
			.handle(result, result, retrieveConfiguration(result));
	}

	private static Map<String, Object> retrieveConfiguration(ExchangeResult result) {
		Map<String, Object> configuration = RestTestClientRestDocumentationConfigurer
			.retrieveConfiguration(result.getRequestHeaders());
		String uriTemplate = result.getUriTemplate();
		if (uriTemplate != null) {
			configuration.put(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, uriTemplate);
		}
		return configuration;
	}

}
