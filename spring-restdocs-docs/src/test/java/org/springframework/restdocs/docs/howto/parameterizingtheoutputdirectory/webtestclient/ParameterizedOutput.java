/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.howto.parameterizingtheoutputdirectory.webtestclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
class ParameterizedOutput {

	// @fold:on // Fields
	@SuppressWarnings("unused")
	private WebTestClient webTestClient;

	@Autowired
	private ApplicationContext context;

	// @fold:off

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {
		this.webTestClient = WebTestClient.bindToApplicationContext(this.context)
			.configureClient()
			.filter(documentationConfiguration(restDocumentation))
			.entityExchangeResultConsumer(document("{method-name}/{step}"))
			.build();
	}

}
