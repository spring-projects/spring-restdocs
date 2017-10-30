/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.webtestclient;

import com.example.SnippetReuse;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class WebTestClientSnippetReuse extends SnippetReuse {

	// @formatter:off

	private WebTestClient webTestClient;

	public void documentation() throws Exception {
		// tag::use[]
		this.webTestClient.get().uri("/").accept(MediaType.APPLICATION_JSON).exchange()
			.expectStatus().isOk().expectBody()
			.consumeWith(document("example", this.pagingLinks.and( // <1>
				linkWithRel("alpha").description("Link to the alpha resource"),
				linkWithRel("bravo").description("Link to the bravo resource"))));
		// end::use[]
	}

}
