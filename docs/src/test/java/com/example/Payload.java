/*
 * Copyright 2014-2015 the original author or authors.
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

package com.example;

import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldType;
import org.springframework.test.web.servlet.MockMvc;

public class Payload {

private MockMvc mockMvc;

	public void response() throws Exception {
		// tag::response[]
		this.mockMvc.perform(get("/user/5").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("index").withResponseFields( // <1>
					fieldWithPath("contact").description("The user's contact details"), // <2>
					fieldWithPath("contact.email").description("The user's email address"))); // <3>
		// end::response[]
	}

	public void explicitType() throws Exception {
		this.mockMvc.perform(get("/user/5").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			// tag::explicit-type[]
			.andDo(document("index").withResponseFields(
					fieldWithPath("contact.email")
							.type(FieldType.STRING) // <1>
							.optional()
							.description("The user's email address")));
			// end::explicit-type[]
	}

	public void constraints() throws Exception {
		this.mockMvc.perform(post("/users/").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			// tag::constraints[]
			.andDo(document("create-user").withRequestFields(
					fieldWithPath("name")
							.description("The user's name")
							.attribute("constraints", "Must not be null. Must not be empty"),
					fieldWithPath("email")
							.description("The user's email address")
							.attribute("constrains", "Must be a valid email address")));
			// end::constraints[]
	}

}
