/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.pathparameters.resttestclient;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.client.RestTestClient;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.document;

class PathParameters {

	// @fold:on // Fields

	private RestTestClient restTestClient;

	// @fold:off

	@Test
	void test() {
		this.restTestClient.get()
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
