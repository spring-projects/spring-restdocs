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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

public class CustomUriConfiguration {

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@SuppressWarnings("unused")
	private WebTestClient webTestClient;

	@Autowired
	private WebApplicationContext context;

	// tag::custom-uri-configuration[]
	@Before
	public void setUp() {
		this.webTestClient = WebTestClient.bindToApplicationContext(this.context)
			.configureClient()
			.baseUrl("https://api.example.com") // <1>
			.filter(documentationConfiguration(this.restDocumentation)).build();
	}
	// end::custom-uri-configuration[]

}
