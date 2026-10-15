/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.gettingstarted.documentationsnippets.invokingtheservice.resttestclient;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.document;

class InvokeService {

	// @fold:on // Fields
	private RestTestClient restTestClient;

	// @fold:off

	void invokeService() {
		this.restTestClient.get()
			.uri("/")
			.accept(MediaType.APPLICATION_JSON) // <1>
			.exchange()
			.expectStatus()
			.isOk() // <2>
			.expectBody()
			.consumeWith(document("index")); // <3>
	}

}
