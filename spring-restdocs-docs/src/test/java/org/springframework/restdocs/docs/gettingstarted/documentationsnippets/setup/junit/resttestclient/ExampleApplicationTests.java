/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.gettingstarted.documentationsnippets.setup.junit.resttestclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
class ExampleApplicationTests {

	@SuppressWarnings("unused")
	private RestTestClient restTestClient;

	@BeforeEach
	void setUp(WebApplicationContext applicationContext, RestDocumentationContextProvider restDocumentation) {
		this.restTestClient = RestTestClient.bindToApplicationContext(applicationContext)
			.requestInterceptor(documentationConfiguration(restDocumentation)) // <1>
			.build();
	}

}
