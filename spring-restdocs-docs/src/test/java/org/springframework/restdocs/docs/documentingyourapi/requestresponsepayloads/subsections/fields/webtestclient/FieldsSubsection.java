/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.requestresponsepayloads.subsections.fields.webtestclient;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

class FieldsSubsection {

	// @fold:on // Fields
	private WebTestClient webTestClient;

	// @fold:off

	@Test
	void test() {
		this.webTestClient.get()
			.uri("/locations/1")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("temperature", responseFields(beneathPath("weather.temperature"), // <1>
					fieldWithPath("high").description("The forecast high in degrees celcius"), // <2>
					fieldWithPath("low").description("The forecast low in degrees celcius"))));
	}

}
