/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.multipart.requestpartpayloads.fields.mockmvc;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RequestPartFields {

	// @fold:on // Fields
	private MockMvc mockMvc;

	// @fold:off

	@Test
	void test() throws Exception {
		MockMultipartFile image = new MockMultipartFile("image", "image.png", "image/png", "<<png data>>".getBytes());
		MockMultipartFile metadata = new MockMultipartFile("metadata", "", "application/json",
				"{ \"version\": \"1.0\"}".getBytes());

		this.mockMvc.perform(multipart("/images").file(image).file(metadata).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("image-upload", requestPartFields("metadata", // <1>
					fieldWithPath("version").description("The version of the image")))); // <2>
	}

}
