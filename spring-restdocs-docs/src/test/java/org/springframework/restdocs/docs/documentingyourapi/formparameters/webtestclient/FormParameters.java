/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.formparameters.webtestclient;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class FormParameters {

	// @fold:on // Fields
	private WebTestClient webTestClient;

	// @fold:off

	@Test
	void test() {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("username", "Tester");
		this.webTestClient.post()
			.uri("/users")
			.body(BodyInserters.fromFormData(formData)) // <1>
			.exchange()
			.expectStatus()
			.isCreated()
			.expectBody()
			.consumeWith(document("create-user", formParameters(// <2>
					parameterWithName("username").description("The user's username") // <3>
			)));
	}

}
