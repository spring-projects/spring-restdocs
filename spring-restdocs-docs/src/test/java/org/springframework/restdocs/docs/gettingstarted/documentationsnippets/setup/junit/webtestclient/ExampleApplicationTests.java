/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.gettingstarted.documentationsnippets.setup.junit.webtestclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
class ExampleApplicationTests {

	@SuppressWarnings("unused")
	private WebTestClient webTestClient;

	@BeforeEach
	void setUp(ApplicationContext applicationContext, RestDocumentationContextProvider restDocumentation) {
		this.webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
			.configureClient()
			.filter(documentationConfiguration(restDocumentation)) // <1>
			.build();
	}

}
