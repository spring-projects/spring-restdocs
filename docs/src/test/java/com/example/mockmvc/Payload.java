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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

public class Payload {

	private MockMvc mockMvc;

	public void response() throws Exception {
		// tag::response[]
		this.mockMvc.perform(get("/user/5").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("index", responseFields( // <1>
					fieldWithPath("contact").description("The user's contact details"), // <2>
					fieldWithPath("contact.email").description("The user's email address")))); // <3>
		// end::response[]
	}

	public void explicitType() throws Exception {
		this.mockMvc.perform(get("/user/5").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			// tag::explicit-type[]
			.andDo(document("index", responseFields(
					fieldWithPath("contact.email")
							.type(JsonFieldType.STRING) // <1>
							.description("The user's email address"))));
			// end::explicit-type[]
	}

	public void constraints() throws Exception {
		this.mockMvc.perform(post("/users/").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			// tag::constraints[]
			.andDo(document("create-user", requestFields(
					attributes(key("title").value("Fields for user creation")), // <1>
					fieldWithPath("name").description("The user's name")
							.attributes(key("constraints")
									.value("Must not be null. Must not be empty")), // <2>
					fieldWithPath("email").description("The user's email address")
							.attributes(key("constraints")
									.value("Must be a valid email address"))))); // <3>
			// end::constraints[]
	}

	public void descriptorReuse() throws Exception {
		FieldDescriptor[] book = new FieldDescriptor[] {
				fieldWithPath("title").description("Title of the book"),
				fieldWithPath("author").description("Author of the book") };

		// tag::single-book[]
		this.mockMvc.perform(get("/books/1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("book", responseFields(book))); // <1>
				// end::single-book[]

		// tag::book-array[]
		this.mockMvc.perform(get("/books").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("book", responseFields(
						fieldWithPath("[]").description("An array of books")) // <1>
						.andWithPrefix("[].", book))); // <2>
		// end::book-array[]
	}

}
