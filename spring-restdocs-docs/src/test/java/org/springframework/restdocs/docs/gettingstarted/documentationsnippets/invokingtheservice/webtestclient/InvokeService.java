/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.gettingstarted.documentationsnippets.invokingtheservice.webtestclient;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

class InvokeService {

	// @fold:on // Fields
	private WebTestClient webTestClient;

	// @fold:off

	void invokeService() {
		this.webTestClient.get()
			.uri("/")
			.accept(MediaType.APPLICATION_JSON) // <1>
			.exchange()
			.expectStatus()
			.isOk() // <2>
			.expectBody()
			.consumeWith(document("index")); // <3>
	}

}
