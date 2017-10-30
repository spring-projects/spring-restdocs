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

import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class PerTestPreprocessing {

	// @formatter:off

	private WebTestClient webTestClient;

	public void general() {
		// tag::preprocessing[]
		this.webTestClient.get().uri("/").exchange().expectStatus().isOk().expectBody()
			.consumeWith(document("index",
				preprocessRequest(removeHeaders("Foo")), // <1>
				preprocessResponse(prettyPrint()))); // <2>
		// end::preprocessing[]
	}

}
