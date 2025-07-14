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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static org.springframework.restdocs.request.RequestDocumentation.formParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class FormParameters {

	// @formatter:off

	private WebTestClient webTestClient;

	public void postFormDataSnippet() {
		// tag::form-parameters[]
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("username", "Tester");
		this.webTestClient.post().uri("/users").body(BodyInserters.fromFormData(formData)) // <1>
				.exchange().expectStatus().isCreated().expectBody()
				.consumeWith(document("create-user", formParameters(// <2>
						parameterWithName("username").description("The user's username") // <3>
				)));
		// end::form-parameters[]
	}

}
