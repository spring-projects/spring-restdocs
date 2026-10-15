/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.requestresponsepayloads.fields.reusingfielddescriptors.webtestclient;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.restdocs.docs.documentingyourapi.requestresponsepayloads.fields.reusingfielddescriptors.BookPayload;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

class ArrayDescriptorReuse {

	// @fold:on // Fields
	private WebTestClient webTestClient;

	// @fold:off

	@Test
	void test() {
		this.webTestClient.get()
			.uri("/books")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("books", responseFields(fieldWithPath("[]").description("An array of books")) // <1>
				.andWithPrefix("[].", BookPayload.bookDescription()))); // <2>
	}

}
