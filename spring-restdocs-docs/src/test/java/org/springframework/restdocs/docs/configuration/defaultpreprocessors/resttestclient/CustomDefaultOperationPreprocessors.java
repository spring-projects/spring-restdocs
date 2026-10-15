/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.configuration.defaultpreprocessors.resttestclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
class CustomDefaultOperationPreprocessors {

	// @fold:on // Fields
	private WebApplicationContext context;

	@SuppressWarnings("unused")
	private RestTestClient restTestClient;

	// @fold:off

	@BeforeEach
	void setup(RestDocumentationContextProvider restDocumentation) {
		this.restTestClient = RestTestClient.bindToApplicationContext(this.context)
			.requestInterceptor(documentationConfiguration(restDocumentation).operationPreprocessors()
				.withRequestDefaults(modifyHeaders().remove("Foo")) // <1>
				.withResponseDefaults(prettyPrint())) // <2>
			.build();
	}

}
