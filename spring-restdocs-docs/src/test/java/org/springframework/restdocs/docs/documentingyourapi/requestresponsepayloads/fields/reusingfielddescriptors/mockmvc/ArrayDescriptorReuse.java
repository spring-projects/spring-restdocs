/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.requestresponsepayloads.fields.reusingfielddescriptors.mockmvc;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.restdocs.docs.documentingyourapi.requestresponsepayloads.fields.reusingfielddescriptors.BookPayload;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArrayDescriptorReuse {

	// @fold:on // Fields
	private MockMvc mockMvc;

	// @fold:off

	@Test
	void test() throws Exception {
		this.mockMvc.perform(get("/books").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("book", responseFields(fieldWithPath("[]").description("An array of books")) // <1>
				.andWithPrefix("[].", BookPayload.bookDescription()))); // <2>
	}

}
