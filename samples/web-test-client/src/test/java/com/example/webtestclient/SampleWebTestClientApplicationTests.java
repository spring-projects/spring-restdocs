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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SampleWebTestClientApplication.class)
public class SampleWebTestClientApplicationTests {

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@Autowired
	ApplicationContext context;

	private WebTestClient webTestClient;

	@Before
	public void setUp() {
		this.webTestClient = WebTestClient.bindToApplicationContext(context)
			.configureClient().baseUrl("https://api.example.com")
			.filter(documentationConfiguration(restDocumentation))
			.build();
	}

	@Test
	public void sample() throws Exception {
		this.webTestClient.get().uri("/").exchange()
			.expectStatus().isOk().expectBody()
			.consumeWith(document("sample"));
	}

}
