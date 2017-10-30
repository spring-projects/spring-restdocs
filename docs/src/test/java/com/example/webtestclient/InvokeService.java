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

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class InvokeService {

	private WebTestClient webTestClient;

	public void invokeService() throws Exception {
		// tag::invoke-service[]
		this.webTestClient.get().uri("/").accept(MediaType.APPLICATION_JSON) // <1>
				.exchange().expectStatus().isOk() // <2>
				.expectBody().consumeWith(document("index")); // <3>
		// end::invoke-service[]
	}

}
