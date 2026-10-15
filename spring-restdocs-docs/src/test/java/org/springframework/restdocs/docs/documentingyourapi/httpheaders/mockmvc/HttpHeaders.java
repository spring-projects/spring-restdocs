/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.httpheaders.mockmvc;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HttpHeaders {

	// @fold:on // Fields
	private MockMvc mockMvc;

	// @fold:off

	@Test
	void test() throws Exception {
		this.mockMvc.perform(get("/people").header("Authorization", "Basic dXNlcjpzZWNyZXQ=")) // <1>
			.andExpect(status().isOk())
			.andDo(document("headers", requestHeaders(// <2>
					headerWithName("Authorization").description("Basic auth credentials")), // <3>
					responseHeaders(// <4>
							headerWithName("X-RateLimit-Limit")
								.description("The total number of requests permitted per period"),
							headerWithName("X-RateLimit-Remaining")
								.description("Remaining requests permitted in current period"),
							headerWithName("X-RateLimit-Reset")
								.description("Time at which the rate limit period will reset"))));
	}

}
