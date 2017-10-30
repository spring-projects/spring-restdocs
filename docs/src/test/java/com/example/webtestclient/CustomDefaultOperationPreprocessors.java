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

import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

public class CustomDefaultOperationPreprocessors {

	// @formatter:off

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	private WebApplicationContext context;

	@SuppressWarnings("unused")
	private WebTestClient webTestClient;

	@Before
	public void setup() {
		// tag::custom-default-operation-preprocessors[]
		this.webTestClient = WebTestClient.bindToApplicationContext(this.context)
			.configureClient()
			.filter(documentationConfiguration(this.restDocumentation)
				.operationPreprocessors()
					.withRequestDefaults(removeHeaders("Foo")) // <1>
					.withResponseDefaults(prettyPrint())) // <2>
			.build();
		// end::custom-default-operation-preprocessors[]
	}

}
