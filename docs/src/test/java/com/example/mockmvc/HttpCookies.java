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

import javax.servlet.http.Cookie;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HttpCookies {

	private MockMvc mockMvc;

	public void cookies() throws Exception {
		// tag::cookies[]
		this.mockMvc
			.perform(get("/").cookie(new Cookie("JSESSIONID", "ACBCDFD0FF93D5BB"))) // <1>
			.andExpect(status().isOk())
			.andDo(document("cookies",
					requestCookies( // <2>
							cookieWithName("JSESSIONID").description(
									"Session token")), // <3>
					responseCookies( // <4>
							cookieWithName("JSESSIONID").description(
									"Updated session token"),
							cookieWithName("logged_in").description(
									"Set to true if the user is currently logged in"))));
		// end::cookies[]
	}
}
