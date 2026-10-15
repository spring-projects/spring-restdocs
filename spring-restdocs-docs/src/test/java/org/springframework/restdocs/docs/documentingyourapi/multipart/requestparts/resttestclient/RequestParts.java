/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.multipart.requestparts.resttestclient;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.document;

class RequestParts {

	// @fold:on // Fields

	private RestTestClient restTestClient;

	// @fold:off

	@Test
	void test() {
		MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
		multipartData.add("file", "example".getBytes());
		this.restTestClient.post()
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
