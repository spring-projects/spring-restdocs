/*
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mockmvc;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.fileUpload;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RequestPartPayload {

	private MockMvc mockMvc;

	public void fields() throws Exception {
		// tag::fields[]
		MockMultipartFile image = new MockMultipartFile("image", "image.png", "image/png",
				"<<png data>>".getBytes());
		MockMultipartFile metadata = new MockMultipartFile("metadata", "",
				"application/json", "{ \"version\": \"1.0\"}".getBytes());

		this.mockMvc.perform(fileUpload("/images").file(image).file(metadata)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("image-upload", requestPartFields("metadata", // <1>
					fieldWithPath("version").description("The version of the image")))); // <2>
		// end::fields[]
	}

	public void body() throws Exception {
		// tag::body[]
		MockMultipartFile image = new MockMultipartFile("image", "image.png", "image/png",
				"<<png data>>".getBytes());
		MockMultipartFile metadata = new MockMultipartFile("metadata", "",
				"application/json", "{ \"version\": \"1.0\"}".getBytes());

		this.mockMvc.perform(fileUpload("/images").file(image).file(metadata)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("image-upload", requestPartBody("metadata"))); // <1>
		// end::body[]
	}

}
