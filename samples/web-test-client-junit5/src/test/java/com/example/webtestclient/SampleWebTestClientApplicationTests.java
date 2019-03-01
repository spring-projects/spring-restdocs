/*
 * Copyright 2014-2019 the original author or authors.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class SampleWebTestClientApplicationTests {
	@Autowired
	private ApplicationContext context;

	private WebTestClient webTestClient;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		this.webTestClient = WebTestClient.bindToApplicationContext(context)
				.configureClient()
				.baseUrl("https://api.example.com")
				.filter(documentationConfiguration(restDocumentation))
				.build();
	}

	@Test
	public void sample() {
		this.webTestClient.get().uri("/").exchange()
				.expectStatus().isOk().expectBody()
				.consumeWith(document("sample"));
	}
}
