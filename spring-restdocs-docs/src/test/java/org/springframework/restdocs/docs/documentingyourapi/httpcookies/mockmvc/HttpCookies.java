/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.httpcookies.mockmvc;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HttpCookies {

	// @fold:on // Fields
	private MockMvc mockMvc;

	// @fold:off

	@Test
	void test() throws Exception {
		this.mockMvc.perform(get("/").cookie(new Cookie("JSESSIONID", "ACBCDFD0FF93D5BB"))) // <1>
			.andExpect(status().isOk())
			.andDo(document("cookies", requestCookies(// <2>
					cookieWithName("JSESSIONID").description("Session token")), // <3>
					responseCookies(// <4>
							cookieWithName("JSESSIONID").description("Updated session token"),
							cookieWithName("logged_in")
								.description("Set to true if the user is currently logged in"))));
	}

}
