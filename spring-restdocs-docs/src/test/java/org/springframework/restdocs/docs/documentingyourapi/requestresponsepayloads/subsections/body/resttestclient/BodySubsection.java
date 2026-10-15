/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.requestresponsepayloads.subsections.body.resttestclient;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.document;

class BodySubsection {

	// @fold:on // Fields
	private RestTestClient restTestClient;

	// @fold:off

	@Test
	void test() {
		this.restTestClient.get()
			.uri("/locations/1")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("temperature", responseBody(beneathPath("weather.temperature")))); // <1>
	}

}
