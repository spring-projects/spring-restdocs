/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.howto.customizing.includingextrainformation.mockmvc;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IncludingExtraInformation {

	// @fold:on // Fields
	private MockMvc mockMvc;

	// @fold:off

	@Test
	void test() throws Exception {
		this.mockMvc.perform(post("/users/").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("create-user", requestFields(attributes(key("title").value("Fields for user creation")), // <1>
					fieldWithPath("name").description("The user's name")
						.attributes(key("constraints").value("Must not be null. Must not be empty")), // <2>
					fieldWithPath("email").description("The user's email address")
						.attributes(key("constraints").value("Must be a valid email address"))))); // <3>
	}

}
