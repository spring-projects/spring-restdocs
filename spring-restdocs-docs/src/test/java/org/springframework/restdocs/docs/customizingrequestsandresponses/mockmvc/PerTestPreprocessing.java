/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.customizingrequestsandresponses.mockmvc;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PerTestPreprocessing {

	// @fold:on // Fields
	private MockMvc mockMvc;

	// @fold:off

	@Test
	void test() throws Exception {
		this.mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andDo(document("index", preprocessRequest(modifyHeaders().remove("Foo")), // <1>
					preprocessResponse(prettyPrint()))); // <2>
	}

}
