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

import java.util.Collections;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class RequestPartPayload {

	// @formatter:off

	private WebTestClient webTestClient;

	public void fields() throws Exception {
		// tag::fields[]
		MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
		Resource imageResource = new ByteArrayResource("<<png data>>".getBytes()) {

			@Override
			public String getFilename() {
				return "image.png";
			}

		};
		multipartData.add("image", imageResource);
		multipartData.add("metadata", Collections.singletonMap("version",  "1.0"));
		this.webTestClient.post().uri("/images").body(BodyInserters.fromMultipartData(multipartData))
			.accept(MediaType.APPLICATION_JSON).exchange()
			.expectStatus().isOk().expectBody()
			.consumeWith(document("image-upload",
				requestPartFields("metadata", // <1>
					fieldWithPath("version").description("The version of the image")))); // <2>
		// end::fields[]
	}

	public void body() throws Exception {
		// tag::body[]
		MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
		Resource imageResource = new ByteArrayResource("<<png data>>".getBytes()) {

			@Override
			public String getFilename() {
				return "image.png";
			}

		};
		multipartData.add("image", imageResource);
		multipartData.add("metadata", Collections.singletonMap("version",  "1.0"));

		this.webTestClient.post().uri("/images").body(BodyInserters.fromMultipartData(multipartData))
			.accept(MediaType.APPLICATION_JSON).exchange()
			.expectStatus().isOk().expectBody()
			.consumeWith(document("image-upload",
					requestPartBody("metadata"))); // <1>
		// end::body[]
	}

}
