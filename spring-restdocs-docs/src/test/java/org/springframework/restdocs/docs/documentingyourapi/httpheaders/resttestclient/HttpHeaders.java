/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.httpheaders.resttestclient;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.client.RestTestClient;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.document;

class HttpHeaders {

	// @fold:on // Fields
	private RestTestClient restTestClient;

	// @fold:off

	@Test
	void test() {
		this.restTestClient.get()
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
