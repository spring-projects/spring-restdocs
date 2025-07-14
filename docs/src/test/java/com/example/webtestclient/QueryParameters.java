/*
 * Copyright 2014-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.webtestclient;

import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class QueryParameters {

	// @formatter:off

	private WebTestClient webTestClient;

	public void getQueryStringSnippet() {
		// tag::query-parameters[]
		this.webTestClient.get().uri("/users?page=2&per_page=100") // <1>
			.exchange().expectStatus().isOk().expectBody()
			.consumeWith(document("users", queryParameters(// <2>
					parameterWithName("page").description("The page to retrieve"), // <3>
					parameterWithName("per_page").description("Entries per page") // <4>
			)));
		// end::query-parameters[]
	}

}
