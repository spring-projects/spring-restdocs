/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.howto.reusingsnippets.resttestclient;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.restdocs.docs.howto.reusingsnippets.CommonSnippets;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.document;

public class ReusingSnippets {

	// @fold:on // Fields
	private RestTestClient restTestClient;

	// @fold:off

	@Test
	void test() {
		this.restTestClient.get()
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
