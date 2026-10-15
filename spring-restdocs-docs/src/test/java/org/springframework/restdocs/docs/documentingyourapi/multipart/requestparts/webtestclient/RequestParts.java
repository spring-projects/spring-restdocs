/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.multipart.requestparts.webtestclient;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

class RequestParts {

	// @fold:on // Fields

	private WebTestClient webTestClient;

	// @fold:off

	@Test
	void test() {
		MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
		multipartData.add("file", "example".getBytes());
		this.webTestClient.post()
			.uri("/upload")
			.body(BodyInserters.fromMultipartData(multipartData)) // <1>
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("upload", requestParts(// <2>
					partWithName("file").description("The file to upload")) // <3>
			));
	}

}
