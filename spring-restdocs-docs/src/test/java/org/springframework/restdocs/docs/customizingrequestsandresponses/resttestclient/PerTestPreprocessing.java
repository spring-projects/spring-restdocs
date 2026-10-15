/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.customizingrequestsandresponses.resttestclient;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.client.RestTestClient;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.document;

class PerTestPreprocessing {

	// @fold:on // Fields
	private RestTestClient restTestClient;

	// @fold:off

	@Test
	void test() {
		this.restTestClient.get()
			.uri("/")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("index", preprocessRequest(modifyHeaders().remove("Foo")), // <1>
					preprocessResponse(prettyPrint()))); // <2>
	}

}
