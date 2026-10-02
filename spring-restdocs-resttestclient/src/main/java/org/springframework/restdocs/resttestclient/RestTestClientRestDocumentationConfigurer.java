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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.config.RestDocumentationConfigurer;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A {@link RestTestClient}-specific {@link RestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 * @since 4.1.0
 */
public class RestTestClientRestDocumentationConfigurer extends
		RestDocumentationConfigurer<RestTestClientSnippetConfigurer, RestTestClientOperationPreprocessorsConfigurer, RestTestClientRestDocumentationConfigurer>
		implements ClientHttpRequestInterceptor {

	private final RestTestClientSnippetConfigurer snippetConfigurer = new RestTestClientSnippetConfigurer(this);

	private static final Map<String, Map<String, Object>> configurations = new ConcurrentHashMap<>();

	private final RestTestClientOperationPreprocessorsConfigurer operationPreprocessorsConfigurer = new RestTestClientOperationPreprocessorsConfigurer(
			this);

	private final RestDocumentationContextProvider contextProvider;

	RestTestClientRestDocumentationConfigurer(RestDocumentationContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	@Override
	public RestTestClientSnippetConfigurer snippets() {
		return this.snippetConfigurer;
	}

	@Override
	public RestTestClientOperationPreprocessorsConfigurer operationPreprocessors() {
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
		String requestId = headers.getFirst(RestTestClient.RESTTESTCLIENT_REQUEST_ID);
		Map<String, Object> configuration = configurations.remove(requestId);
		Assert.state(configuration != null, () -> "REST Docs configuration not found. Did you forget to register a "
				+ RestTestClientRestDocumentationConfigurer.class.getSimpleName() + " as an interceptor?");
		return configuration;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		String index = request.getHeaders().getFirst(RestTestClient.RESTTESTCLIENT_REQUEST_ID);
		Assert.state(index != null, "Missing %s header".formatted(RestTestClient.RESTTESTCLIENT_REQUEST_ID));
		configurations.put(index, createConfiguration());
		return execution.execute(applyUriDefaults(request), body);
	}

	private HttpRequest applyUriDefaults(HttpRequest request) {
		URI requestUri = request.getURI();
		if (StringUtils.hasLength(requestUri.getHost())) {
			return request;
		}
		try {
			return new HttpRequestWrapper(request) {

				private final URI uri = new URI("http", requestUri.getUserInfo(), "localhost", 8080,
						requestUri.getPath(), requestUri.getQuery(), requestUri.getFragment());

				@Override
				public URI getURI() {
					return this.uri;
				}

			};
		}
		catch (URISyntaxException ex) {
			throw new IllegalStateException(ex);
		}
	}

}
