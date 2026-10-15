/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.howto.reusingsnippets.webtestclient;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.restdocs.docs.howto.reusingsnippets.CommonSnippets;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class ReusingSnippets {

	// @fold:on // Fields
	private WebTestClient webTestClient;

	// @fold:off

	@Test
	void test() {
		this.webTestClient.get()
			.uri("/")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("example", CommonSnippets.pagingLinks.and(// <1>
					linkWithRel("alpha").description("Link to the alpha resource"),
					linkWithRel("bravo").description("Link to the bravo resource"))));
	}

}
