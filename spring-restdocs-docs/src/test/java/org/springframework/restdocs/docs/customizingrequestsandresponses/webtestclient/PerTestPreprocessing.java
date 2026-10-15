/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.customizingrequestsandresponses.webtestclient;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

class PerTestPreprocessing {

	// @fold:on // Fields
	private WebTestClient webTestClient;

	// @fold:off

	@Test
	void test() {
		this.webTestClient.get()
			.uri("/")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("index", preprocessRequest(modifyHeaders().remove("Foo")), // <1>
					preprocessResponse(prettyPrint()))); // <2>
	}

}
