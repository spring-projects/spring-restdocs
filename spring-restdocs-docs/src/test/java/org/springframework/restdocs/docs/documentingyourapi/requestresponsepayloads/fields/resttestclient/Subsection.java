/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.requestresponsepayloads.fields.resttestclient;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.document;

class Subsection {

	// @fold:on // Fields
	private RestTestClient restTestClient;

	// @fold:off

	@Test
	void subsection() {
		this.restTestClient.get()
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
