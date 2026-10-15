/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.configuration.defaultsnippets.mockmvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
class CustomDefaultSnippets {

	// @fold:on // Fields
	@Autowired
	private WebApplicationContext context;

	@SuppressWarnings("unused")
	private MockMvc mockMvc;

	// @fold:off

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(restDocumentation).snippets().withDefaults(curlRequest()))
			.build();
	}

}
