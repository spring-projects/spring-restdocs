/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.httpheaders.webtestclient;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

class HttpHeaders {

	// @fold:on // Fields
	private WebTestClient webTestClient;

	// @fold:off

	@Test
	void test() {
		this.webTestClient.get()
			.uri("/people")
			.header("Authorization", "Basic dXNlcjpzZWNyZXQ=") // <1>
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("headers", requestHeaders(// <2>
					headerWithName("Authorization").description("Basic auth credentials")), // <3>
					responseHeaders(// <4>
							headerWithName("X-RateLimit-Limit")
								.description("The total number of requests permitted per period"),
							headerWithName("X-RateLimit-Remaining")
								.description("Remaining requests permitted in current period"),
							headerWithName("X-RateLimit-Reset")
								.description("Time at which the rate limit period will reset"))));
	}

}
