/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.howto.parameterizingtheoutputdirectory.resttestclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.document;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
class ParameterizedOutput {

	// @fold:on // Fields
	@SuppressWarnings("unused")
	private RestTestClient restTestClient;

	@Autowired
	private WebApplicationContext context;

	// @fold:off

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {
		this.restTestClient = RestTestClient.bindToApplicationContext(this.context)
			.requestInterceptor(documentationConfiguration(restDocumentation))
			.entityExchangeResultConsumer(document("{method-name}/{step}"))
			.build();
	}

}
