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

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HttpHeaders {

	private MockMvc mockMvc;

	public void headers() throws Exception {
		// tag::headers[]
		this.mockMvc
			.perform(get("/people").header("Authorization", "Basic dXNlcjpzZWNyZXQ=")) // <1>
			.andExpect(status().isOk())
			.andDo(document("headers",
					requestHeaders( // <2>
							headerWithName("Authorization").description(
									"Basic auth credentials")), // <3>
					responseHeaders( // <4>
							headerWithName("X-RateLimit-Limit").description(
									"The total number of requests permitted per period"),
							headerWithName("X-RateLimit-Remaining").description(
									"Remaining requests permitted in current period"),
							headerWithName("X-RateLimit-Reset").description(
									"Time at which the rate limit period will reset"))));
		// end::headers[]
	}
}
