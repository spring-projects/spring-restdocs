/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.configuration.defaultsnippets.webtestclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
class CustomDefaultSnippets {

	// @fold:on // Fields
	@Autowired
	private ApplicationContext context;

	@SuppressWarnings("unused")
	private WebTestClient webTestClient;

	// @fold:off

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {
		this.webTestClient = WebTestClient.bindToApplicationContext(this.context)
			.configureClient()
			.filter(documentationConfiguration(restDocumentation).snippets().withDefaults(curlRequest()))
			.build();
	}

}
