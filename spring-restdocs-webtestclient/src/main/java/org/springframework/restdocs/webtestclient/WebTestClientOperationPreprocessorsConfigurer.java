/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.webtestclient;

import reactor.core.publisher.Mono;

import org.springframework.restdocs.config.OperationPreprocessorsConfigurer;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

/**
 * A configurer that can be used to configure the operation preprocessors.
 *
 * @author Andy Wilkinson
 * @since 2.0.0
 */
public final class WebTestClientOperationPreprocessorsConfigurer extends
		OperationPreprocessorsConfigurer<WebTestClientRestDocumentationConfigurer, WebTestClientOperationPreprocessorsConfigurer>
		implements ExchangeFilterFunction {

	WebTestClientOperationPreprocessorsConfigurer(WebTestClientRestDocumentationConfigurer parent) {
		super(parent);
	}

	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
		return and().filter(request, next);
	}

}
