/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.howto.customizing.includingextrainformation.webtestclient;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

class IncludingExtraInformation {

	// @fold:on // Fields
	private WebTestClient webTestClient;

	// @fold:off

	@Test
	void test() {
		this.webTestClient.get()
			.uri("user/5")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("create-user",
					requestFields(attributes(key("title").value("Fields for user creation")), // <1>
							fieldWithPath("name").description("The user's name")
								.attributes(key("constraints").value("Must not be null. Must not be empty")), // <2>
							fieldWithPath("email").description("The user's email address")
								.attributes(key("constraints").value("Must be a valid email address"))))); // <3>
	}

}
