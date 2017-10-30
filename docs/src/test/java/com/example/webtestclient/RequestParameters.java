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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class RequestParameters {

	// @formatter:off

	private WebTestClient webTestClient;

	public void getQueryStringSnippet() throws Exception {
		// tag::request-parameters-query-string[]
		this.webTestClient.get().uri("/users?page=2&per_page=100") // <1>
			.exchange().expectStatus().isOk().expectBody()
			.consumeWith(document("users", requestParameters( // <2>
					parameterWithName("page").description("The page to retrieve"), // <3>
					parameterWithName("per_page").description("Entries per page") // <4>
			)));
		// end::request-parameters-query-string[]
	}

	public void postFormDataSnippet() throws Exception {
		// tag::request-parameters-form-data[]
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("username", "Tester");
		this.webTestClient.post().uri("/users").body(BodyInserters.fromFormData(formData)) // <1>
			.exchange().expectStatus().isCreated().expectBody()
			.consumeWith(document("create-user", requestParameters(
				parameterWithName("username").description("The user's username")
		)));
		// end::request-parameters-form-data[]
	}

}
