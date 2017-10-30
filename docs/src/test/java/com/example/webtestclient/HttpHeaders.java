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

import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class HttpHeaders {

	// @formatter:off

	private WebTestClient webTestClient;

	public void headers() throws Exception {
		// tag::headers[]
		this.webTestClient
			.get().uri("/people").header("Authorization", "Basic dXNlcjpzZWNyZXQ=") // <1>
			.exchange().expectStatus().isOk().expectBody()
			.consumeWith(document("headers",
				requestHeaders( // <2>
					headerWithName("Authorization").description("Basic auth credentials")), // <3>
				responseHeaders( // <4>
					headerWithName("X-RateLimit-Limit")
						.description("The total number of requests permitted per period"),
					headerWithName("X-RateLimit-Remaining")
						.description("Remaining requests permitted in current period"),
					headerWithName("X-RateLimit-Reset")
						.description("Time at which the rate limit period will reset"))));
		// end::headers[]
	}
}
