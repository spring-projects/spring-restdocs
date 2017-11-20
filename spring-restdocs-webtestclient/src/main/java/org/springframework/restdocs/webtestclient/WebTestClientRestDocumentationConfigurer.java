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

package org.springframework.restdocs.webtestclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import reactor.core.publisher.Mono;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.config.RestDocumentationConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

/**
 * A WebFlux-specific {@link RestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 * @since 2.0.0
 */
public class WebTestClientRestDocumentationConfigurer extends
		RestDocumentationConfigurer<WebTestClientSnippetConfigurer, WebTestClientOperationPreprocessorsConfigurer, WebTestClientRestDocumentationConfigurer>
		implements ExchangeFilterFunction {

	private final WebTestClientSnippetConfigurer snippetConfigurer = new WebTestClientSnippetConfigurer(
			this);

	private static final Map<String, Map<String, Object>> configurations = new ConcurrentHashMap<>();

	private final WebTestClientOperationPreprocessorsConfigurer operationPreprocessorsConfigurer = new WebTestClientOperationPreprocessorsConfigurer(
			this);

	private final RestDocumentationContextProvider contextProvider;

	WebTestClientRestDocumentationConfigurer(
			RestDocumentationContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	@Override
	public WebTestClientSnippetConfigurer snippets() {
		return this.snippetConfigurer;
	}

	@Override
	public WebTestClientOperationPreprocessorsConfigurer operationPreprocessors() {
		return this.operationPreprocessorsConfigurer;
	}

	private Map<String, Object> createConfiguration() {
		RestDocumentationContext context = this.contextProvider.beforeOperation();
		Map<String, Object> configuration = new HashMap<>();
		configuration.put(RestDocumentationContext.class.getName(), context);
		apply(configuration, context);
		return configuration;
	}

	static Map<String, Object> retrieveConfiguration(HttpHeaders headers) {
		String requestId = headers.getFirst(WebTestClient.WEBTESTCLIENT_REQUEST_ID);
		return configurations.remove(requestId);
	}

	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
		String index = request.headers().getFirst(WebTestClient.WEBTESTCLIENT_REQUEST_ID);
		configurations.put(index, createConfiguration());
		return next.exchange(applyUriDefaults(request));
	}

	private ClientRequest applyUriDefaults(ClientRequest request) {
		URI requestUri = request.url();
		if (!StringUtils.isEmpty(requestUri.getHost())) {
			return request;
		}
		try {
			requestUri = new URI("http", requestUri.getUserInfo(), "localhost", 8080,
					requestUri.getPath(), requestUri.getQuery(),
					requestUri.getFragment());
			return ClientRequest.from(request).url(requestUri).build();
		}
		catch (URISyntaxException ex) {
			throw new IllegalStateException(ex);
		}
	}

}
