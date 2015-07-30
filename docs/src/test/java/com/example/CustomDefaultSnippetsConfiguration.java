package com.example;

import static org.springframework.restdocs.RestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.curl.CurlDocumentation.curlRequest;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class CustomDefaultSnippetsConfiguration {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		// tag::custom-default-snippets[]
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration().snippets()
						.withDefaults(curlRequest()))
				.build();
		// end::custom-default-snippets[]
	}

}
