/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.pathparameters.webtestclient;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

class PathParameters {

	// @fold:on // Fields

	private WebTestClient webTestClient;

	// @fold:off

	@Test
	void test() {
		this.webTestClient.get()
			.uri("/locations/{latitude}/{longitude}", 51.5072, 0.1275) // <1>
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("locations", pathParameters(// <2>
					parameterWithName("latitude").description("The location's latitude"), // <3>
					parameterWithName("longitude").description("The location's longitude")))); // <4>
	}

}
