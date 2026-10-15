/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.gettingstarted.documentationsnippets.invokingtheservice.mockmvc;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InvokeService {

	// @fold:on // Fields

	private MockMvc mockMvc;

	// @fold:off

	void test() throws Exception {
		this.mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)) // <1>
			.andExpect(status().isOk()) // <2>
			.andDo(document("index")); // <3>
	}

}
