/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.pathparameters.mockmvc;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PathParameters {

	// @fold:on // Fields
	private MockMvc mockMvc;

	// @fold:off

	@Test
	void test() throws Exception {
		this.mockMvc.perform(get("/locations/{latitude}/{longitude}", 51.5072, 0.1275)) // <1>
			.andExpect(status().isOk())
			.andDo(document("locations", pathParameters(// <2>
					parameterWithName("latitude").description("The location's latitude"), // <3>
					parameterWithName("longitude").description("The location's longitude") // <4>
			)));
	}

}
