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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;

public class QueryParameters {

	private MockMvc mockMvc;
	
	public void queryParameters() throws Exception {
		// tag::query-parameters[]
		this.mockMvc.perform(get("/users?page=2&per_page=100"))
			.andExpect(status().isOk())
			.andDo(document("users").withQueryParameters( // <1>
					parameterWithName("page").description("The page to retrieve"), // <2>
					parameterWithName("per_page").description("Entries per page") // <3>
			));
		// end::query-parameters[]
	}
	
}
