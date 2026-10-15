/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.requestresponsepayloads.fields.json.fieldtypes.webtestclient;

import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

class ExplicitFieldType {

	// @fold:on // Fields
	private WebTestClient webTestClient;

	// @fold:off

	void test() {
		this.webTestClient.get()
			.uri("user/5")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("user", responseFields(fieldWithPath("contact.email").type(JsonFieldType.STRING) // <1>
				.description("The user's email address"))));
	}

}
