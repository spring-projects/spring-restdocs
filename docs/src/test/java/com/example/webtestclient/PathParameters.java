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

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class PathParameters {

	// @formatter:off

	private WebTestClient webTestClient;

	public void pathParametersSnippet() throws Exception {
		// tag::path-parameters[]
		this.webTestClient.get().uri("/locations/{latitude}/{longitude}", 51.5072, 0.1275) // <1>
			.exchange().expectStatus().isOk().expectBody()
			.consumeWith(document("locations",
				pathParameters( // <2>
					parameterWithName("latitude").description("The location's latitude"), // <3>
					parameterWithName("longitude").description("The location's longitude")))); // <4>
		// end::path-parameters[]
	}

}
