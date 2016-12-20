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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;

public class RequestParameters {

	private MockMvc mockMvc;

	public void getQueryStringSnippet() throws Exception {
		// tag::request-parameters-query-string[]
		this.mockMvc.perform(get("/users?page=2&per_page=100")) // <1>
			.andExpect(status().isOk())
			.andDo(document("users", requestParameters( // <2>
					parameterWithName("page").description("The page to retrieve"), // <3>
					parameterWithName("per_page").description("Entries per page") // <4>
			)));
		// end::request-parameters-query-string[]
	}

	public void postFormDataSnippet() throws Exception {
		// tag::request-parameters-form-data[]
		this.mockMvc.perform(post("/users").param("username", "Tester")) // <1>
			.andExpect(status().isCreated())
			.andDo(document("create-user", requestParameters(
					parameterWithName("username").description("The user's username")
			)));
		// end::request-parameters-form-data[]
	}

}
