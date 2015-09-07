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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;

public class PathParameters {

	private MockMvc mockMvc;

	public void pathParametersSnippet() throws Exception {
		// tag::path-parameters[]
		this.mockMvc.perform(get("/locations/{latitude}/{longitude}", 51.5072, 0.1275)) // <1>
			.andExpect(status().isOk())
			.andDo(document("locations", pathParameters( // <2>
					parameterWithName("latitude").description("The location's latitude"), // <3>
					parameterWithName("longitude").description("The location's longitude") // <4>
			)));
		// end::path-parameters[]
	}

}
