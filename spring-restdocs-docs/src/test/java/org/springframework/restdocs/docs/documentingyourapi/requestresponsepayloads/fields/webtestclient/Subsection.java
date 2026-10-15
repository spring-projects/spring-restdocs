/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.requestresponsepayloads.fields.webtestclient;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

class Subsection {

	// @fold:on // Fields
	private WebTestClient webTestClient;

	// @fold:off

	@Test
	void subsection() {
		this.webTestClient.get()
			.uri("user/5")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("user",
					responseFields(subsectionWithPath("contact").description("The user's contact details")))); // <1>
	}

}
