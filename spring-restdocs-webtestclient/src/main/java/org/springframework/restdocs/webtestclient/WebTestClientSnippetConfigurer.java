/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.webtestclient;

import reactor.core.publisher.Mono;

import org.springframework.restdocs.config.SnippetConfigurer;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

/**
 * A {@link SnippetConfigurer} for WebFlux that can be used to configure the generated
 * documentation snippets.
 *
 * @author Andy Wilkinson
 * @since 2.0.0
 */
public class WebTestClientSnippetConfigurer
		extends SnippetConfigurer<WebTestClientRestDocumentationConfigurer, WebTestClientSnippetConfigurer>
		implements ExchangeFilterFunction {

	WebTestClientSnippetConfigurer(WebTestClientRestDocumentationConfigurer parent) {
		super(parent);
	}

	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
		return and().filter(request, next);
	}

}
