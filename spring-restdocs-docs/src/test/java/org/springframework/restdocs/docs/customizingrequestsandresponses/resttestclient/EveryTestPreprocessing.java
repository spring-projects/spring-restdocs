/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.customizingrequestsandresponses.resttestclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
class EveryTestPreprocessing {

	// @fold:on // Fields
	@Autowired
	private WebApplicationContext context;

	@SuppressWarnings("unused")
	private RestTestClient restTestClient;

	// @fold:off

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {
		this.restTestClient = RestTestClient.bindToApplicationContext(this.context)
			.requestInterceptor(documentationConfiguration(restDocumentation).operationPreprocessors()
				.withRequestDefaults(modifyHeaders().remove("Foo")) // <1>
				.withResponseDefaults(prettyPrint())) // <2>
			.build();
	}

}
